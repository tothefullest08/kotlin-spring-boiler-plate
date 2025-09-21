package harry.boilerplate.shop.command.domain.aggregate

import harry.boilerplate.common.domain.entity.AggregateRoot
import harry.boilerplate.common.domain.entity.Money
import harry.boilerplate.shop.command.domain.entity.OptionGroup
import harry.boilerplate.shop.command.domain.event.MenuOpenedEvent
import harry.boilerplate.shop.command.domain.exception.MenuDomainException
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode
import harry.boilerplate.shop.command.domain.valueObject.MenuId
import harry.boilerplate.shop.command.domain.valueObject.OptionGroupId
import harry.boilerplate.shop.command.domain.valueObject.ShopId
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "menu")
class Menu private constructor(
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private var idValue: String,
    @Column(name = "shop_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private var shopIdValue: String,
    @Column(name = "name", nullable = false)
    private var nameValue: String,
    @Column(name = "description")
    private var descriptionValue: String?,
    @Column(name = "base_price", precision = 10, scale = 2)
    private var basePriceValue: BigDecimal,
    @Column(name = "is_open", nullable = false)
    private var open: Boolean,
    @OneToMany(
        mappedBy = "menu",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private var optionGroupsInternal: MutableList<OptionGroup>
) : AggregateRoot<Menu, MenuId>() {

    override val id: MenuId
        get() = MenuId.of(idValue)

    val shopId: ShopId
        get() = ShopId.of(shopIdValue)

    val name: String
        get() = nameValue

    val description: String?
        get() = descriptionValue

    val basePrice: Money
        get() = Money.of(basePriceValue)

    fun isOpen(): Boolean = open

    val optionGroups: List<OptionGroup>
        get() = optionGroupsInternal.toList()

    fun open() {
        if (open) {
            throw MenuDomainException(MenuErrorCode.MENU_ALREADY_OPEN)
        }
        validateOpenConditions(optionGroupsInternal)
        open = true
        registerEvent(
            MenuOpenedEvent(
                aggregateId = idValue,
                shopId = shopIdValue,
                menuName = nameValue,
                description = descriptionValue
            )
        )
    }

    fun addOptionGroup(name: String?, required: Boolean) {
        if (name.isNullOrBlank()) {
            throw MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED)
        }
        ensureUniqueGroupName(name)
        if (open && required) {
            val requiredCount = optionGroupsInternal.count { it.isRequired }
            if (requiredCount >= 3) {
                throw MenuDomainException(MenuErrorCode.MAX_REQUIRED_OPTION_GROUPS_EXCEEDED)
            }
        }
        val optionGroup = OptionGroup.create(this, OptionGroupId.generate(), name.trim(), required)
        optionGroupsInternal += optionGroup
    }

    fun changeOptionGroupName(optionGroupId: OptionGroupId?, newName: String?) {
        if (optionGroupId == null) {
            throw MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED)
        }
        if (newName.isNullOrBlank()) {
            throw MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED)
        }
        val target = findOptionGroup(optionGroupId)
        val duplicate = optionGroupsInternal.any { it.id != optionGroupId && it.name == newName.trim() }
        if (duplicate) {
            throw MenuDomainException(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME)
        }
        target.changeName(newName)
    }

    fun changeOptionName(optionGroupId: OptionGroupId?, currentName: String?, currentPrice: Money?, newName: String?) {
        if (optionGroupId == null) {
            throw MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED)
        }
        if (currentName.isNullOrBlank()) {
            throw MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED)
        }
        if (currentPrice == null) {
            throw MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED)
        }
        if (newName.isNullOrBlank()) {
            throw MenuDomainException(MenuErrorCode.NEW_OPTION_NAME_REQUIRED)
        }
        val group = findOptionGroup(optionGroupId)
        group.changeOptionName(currentName, currentPrice, newName)
    }

    fun removeOptionGroup(optionGroupId: OptionGroupId?) {
        if (optionGroupId == null) {
            throw MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED)
        }
        val targetGroup = findOptionGroup(optionGroupId)
        if (open) {
            validateRemovalConditions(targetGroup)
        }
        optionGroupsInternal.removeIf { it.id == optionGroupId }
    }

    private fun ensureUniqueGroupName(newName: String) {
        val exists = optionGroupsInternal.any { it.name == newName.trim() }
        if (exists) {
            throw MenuDomainException(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME)
        }
    }

    private fun validateOpenConditions(groups: List<OptionGroup>) {
        if (groups.isEmpty()) {
            throw MenuDomainException(MenuErrorCode.INSUFFICIENT_OPTION_GROUPS)
        }
        val requiredCount = groups.count { it.isRequired }
        if (requiredCount !in 1..3) {
            throw MenuDomainException(MenuErrorCode.INVALID_REQUIRED_OPTION_GROUP_COUNT)
        }
        val hasPaidGroup = groups.any { it.hasPaidOptions() }
        if (!hasPaidGroup) {
            throw MenuDomainException(MenuErrorCode.NO_PAID_OPTION_GROUP)
        }
    }

    private fun validateRemovalConditions(groupToRemove: OptionGroup) {
        val remaining = optionGroupsInternal.filter { it.id != groupToRemove.id }
        if (remaining.isEmpty()) {
            throw MenuDomainException(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP)
        }
        val remainingRequired = remaining.count { it.isRequired }
        if (remainingRequired < 1) {
            throw MenuDomainException(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP)
        }
        val hasPaid = remaining.any { it.hasPaidOptions() }
        if (!hasPaid) {
            throw MenuDomainException(MenuErrorCode.CANNOT_DELETE_REQUIRED_OPTION_GROUP)
        }
    }

    private fun findOptionGroup(optionGroupId: OptionGroupId): OptionGroup =
        optionGroupsInternal.firstOrNull { it.id == optionGroupId }
            ?: throw MenuDomainException(MenuErrorCode.OPTION_GROUP_NOT_FOUND)

    companion object {
        fun create(shopId: ShopId?, name: String?, description: String?, basePrice: Money?): Menu {
            if (shopId == null) {
                throw MenuDomainException(MenuErrorCode.SHOP_ID_REQUIRED)
            }
            if (name.isNullOrBlank()) {
                throw MenuDomainException(MenuErrorCode.MENU_NAME_REQUIRED)
            }
            if (basePrice == null) {
                throw MenuDomainException(MenuErrorCode.BASE_PRICE_REQUIRED)
            }
            if (basePrice.amount < BigDecimal.ZERO) {
                throw MenuDomainException(MenuErrorCode.INVALID_BASE_PRICE)
            }
            return Menu(
                idValue = UUID.randomUUID().toString(),
                shopIdValue = shopId.value,
                nameValue = name.trim(),
                descriptionValue = description?.trim(),
                basePriceValue = basePrice.amount,
                open = false,
                optionGroupsInternal = mutableListOf()
            )
        }
    }

    @Suppress("unused")
    private constructor() : this(
        idValue = "",
        shopIdValue = "",
        nameValue = "",
        descriptionValue = null,
        basePriceValue = BigDecimal.ZERO,
        open = false,
        optionGroupsInternal = mutableListOf()
    )
}

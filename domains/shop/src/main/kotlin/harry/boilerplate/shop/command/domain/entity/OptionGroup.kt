package harry.boilerplate.shop.command.domain.entity

import harry.boilerplate.common.domain.entity.DomainEntity
import harry.boilerplate.common.domain.entity.Money
import harry.boilerplate.shop.command.domain.aggregate.Menu
import harry.boilerplate.shop.command.domain.exception.MenuDomainException
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode
import harry.boilerplate.shop.command.domain.valueObject.Option
import harry.boilerplate.shop.command.domain.valueObject.OptionGroupId
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "option_group")
class OptionGroup private constructor(
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private var idValue: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    val menu: Menu?,
    @Column(name = "name", nullable = false)
    private var nameValue: String,
    @Column(name = "is_required", nullable = false)
    private var required: Boolean,
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "option_group_options",
        joinColumns = [JoinColumn(name = "option_group_id")]
    )
    private var optionsInternal: MutableList<Option>
) : DomainEntity<OptionGroup, OptionGroupId>() {

    override val id: OptionGroupId
        get() = OptionGroupId.of(idValue)

    val name: String
        get() = nameValue

    val isRequired: Boolean
        get() = required

    val options: List<Option>
        get() = optionsInternal.toList()

    fun addOption(option: Option?) {
        if (option == null) {
            throw MenuDomainException(MenuErrorCode.OPTION_GROUP_REQUIRED)
        }
        val exists = optionsInternal.any { existing ->
            existing.name == option.name && existing.price == option.price
        }
        if (exists) {
            throw MenuDomainException(MenuErrorCode.DUPLICATE_OPTION_GROUP_NAME)
        }
        optionsInternal += option
    }

    fun removeOption(optionName: String?, optionPrice: Money?) {
        if (optionName.isNullOrBlank()) {
            throw MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED)
        }
        if (optionPrice == null) {
            throw MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED)
        }

        val target = optionsInternal.firstOrNull { option ->
            option.name == optionName.trim() && option.price == optionPrice
        } ?: throw MenuDomainException(MenuErrorCode.OPTION_NOT_FOUND)

        optionsInternal.remove(target)
    }

    fun changeName(newName: String?) {
        if (newName.isNullOrBlank()) {
            throw MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED)
        }
        nameValue = newName.trim()
    }

    fun changeOptionName(currentName: String?, currentPrice: Money?, newName: String?) {
        if (currentName.isNullOrBlank()) {
            throw MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED)
        }
        if (currentPrice == null) {
            throw MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED)
        }
        if (newName.isNullOrBlank()) {
            throw MenuDomainException(MenuErrorCode.NEW_OPTION_NAME_REQUIRED)
        }

        val index = optionsInternal.indexOfFirst { option ->
            option.name == currentName.trim() && option.price == currentPrice
        }
        if (index < 0) {
            throw MenuDomainException(MenuErrorCode.OPTION_NOT_FOUND)
        }

        val updated = optionsInternal[index].changeName(newName)
        optionsInternal[index] = updated
    }

    fun hasPaidOptions(): Boolean = optionsInternal.any(Option::isPaid)

    fun getOptionCount(): Int = optionsInternal.size

    fun isEmpty(): Boolean = optionsInternal.isEmpty()

    companion object {
        fun create(menu: Menu, id: OptionGroupId, name: String, required: Boolean): OptionGroup {
            if (id == null) {
                throw MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED)
            }
            if (name.isBlank()) {
                throw MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED)
            }
            return OptionGroup(id.value, menu, name.trim(), required, mutableListOf())
        }

        fun createWithoutMenu(id: OptionGroupId, name: String, required: Boolean): OptionGroup {
            if (id == null) {
                throw MenuDomainException(MenuErrorCode.OPTION_GROUP_ID_REQUIRED)
            }
            if (name.isBlank()) {
                throw MenuDomainException(MenuErrorCode.NEW_OPTION_GROUP_NAME_REQUIRED)
            }
            return OptionGroup(id.value, null, name.trim(), required, mutableListOf())
        }
    }

    @Suppress("unused")
    private constructor() : this("", null, "", false, mutableListOf())
}

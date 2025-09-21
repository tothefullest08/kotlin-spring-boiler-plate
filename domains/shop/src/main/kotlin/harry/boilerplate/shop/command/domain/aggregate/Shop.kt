package harry.boilerplate.shop.command.domain.aggregate

import harry.boilerplate.common.domain.entity.AggregateRoot
import harry.boilerplate.common.domain.entity.Money
import harry.boilerplate.shop.command.domain.event.ShopClosedEvent
import harry.boilerplate.shop.command.domain.exception.ShopDomainException
import harry.boilerplate.shop.command.domain.exception.ShopErrorCode
import harry.boilerplate.shop.command.domain.valueObject.BusinessHours
import harry.boilerplate.shop.command.domain.valueObject.ShopId
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.EnumMap
import java.util.UUID

@Entity
@Table(name = "shop")
class Shop private constructor(
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private var idValue: String,
    @Column(name = "name", nullable = false)
    private var nameValue: String,
    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private var minOrderAmountValue: BigDecimal?,
    @Embedded
    private var businessHoursValue: BusinessHours?
) : AggregateRoot<Shop, ShopId>() {

    override val id: ShopId
        get() = ShopId.of(idValue)

    val name: String
        get() = nameValue

    val minOrderAmount: Money
        get() = minOrderAmountValue?.let { Money.of(it) } ?: Money.zero()

    val businessHours: BusinessHours?
        get() = businessHoursValue

    fun isOpen(): Boolean {
        val hours = businessHoursValue ?: return false
        val now = LocalDateTime.now()
        return hours.isOpenAt(now.dayOfWeek, now.toLocalTime())
    }

    fun isOpenAt(time: LocalTime): Boolean {
        val hours = businessHoursValue ?: return false
        val dayOfWeek = LocalDateTime.now().dayOfWeek
        return hours.isOpenAt(dayOfWeek, time)
    }

    fun adjustBusinessHours(openTime: LocalTime?, closeTime: LocalTime?) {
        if (openTime == null || closeTime == null) {
            throw ShopDomainException(ShopErrorCode.INVALID_OPERATING_HOURS)
        }
        if (!openTime.isBefore(closeTime)) {
            throw ShopDomainException(ShopErrorCode.INVALID_OPERATING_HOURS)
        }
        val weeklyHours = EnumMap<DayOfWeek, Pair<LocalTime?, LocalTime?>>(DayOfWeek::class.java)
        DayOfWeek.values().forEach { day -> weeklyHours[day] = Pair(openTime, closeTime) }
        businessHoursValue = BusinessHours.from(weeklyHours)
    }

    fun changeMinOrderAmount(newMinOrderAmount: Money?) {
        if (newMinOrderAmount != null && newMinOrderAmount.amount < BigDecimal.ZERO) {
            throw ShopDomainException(ShopErrorCode.INVALID_MIN_ORDER_AMOUNT)
        }
        minOrderAmountValue = newMinOrderAmount?.amount
    }

    fun close(reason: String?) {
        if (reason.isNullOrBlank()) {
            throw ShopDomainException(ShopErrorCode.CLOSE_REASON_REQUIRED)
        }
        registerEvent(ShopClosedEvent(idValue, nameValue, reason.trim()))
    }

    companion object {
        fun create(name: String?, minOrderAmount: Money?, businessHours: BusinessHours?): Shop {
            if (name.isNullOrBlank()) {
                throw ShopDomainException(ShopErrorCode.SHOP_NAME_REQUIRED)
            }
            if (minOrderAmount != null && minOrderAmount.amount < BigDecimal.ZERO) {
                throw ShopDomainException(ShopErrorCode.INVALID_MIN_ORDER_AMOUNT)
            }
            return Shop(
                idValue = UUID.randomUUID().toString(),
                nameValue = name.trim(),
                minOrderAmountValue = minOrderAmount?.amount,
                businessHoursValue = businessHours
            )
        }
    }

    @Suppress("unused")
    private constructor() : this(
        idValue = "",
        nameValue = "",
        minOrderAmountValue = null,
        businessHoursValue = null
    )
}

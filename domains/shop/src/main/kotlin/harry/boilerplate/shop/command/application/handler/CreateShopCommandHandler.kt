package harry.boilerplate.shop.command.application.handler

import harry.boilerplate.common.domain.entity.Money
import harry.boilerplate.shop.command.application.dto.CreateShopCommand
import harry.boilerplate.shop.command.domain.aggregate.Shop
import harry.boilerplate.shop.command.domain.aggregate.ShopRepository
import harry.boilerplate.shop.command.domain.valueObject.BusinessHours
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.EnumMap

@Component
@Transactional
class CreateShopCommandHandler(
    private val shopRepository: ShopRepository
) {
    fun handle(command: CreateShopCommand): String {
        val minOrderAmount = Money.of(command.minOrderAmount)
        val weeklyHours = EnumMap<DayOfWeek, Pair<LocalTime?, LocalTime?>>(DayOfWeek::class.java)
        val openTime = LocalTime.of(9, 0)
        val closeTime = LocalTime.of(22, 0)
        DayOfWeek.values().forEach { day -> weeklyHours[day] = Pair(openTime, closeTime) }
        val defaultBusinessHours = BusinessHours.from(weeklyHours)

        val shop = Shop.create(
            name = command.name,
            minOrderAmount = minOrderAmount,
            businessHours = defaultBusinessHours
        )
        shopRepository.save(shop)
        return shop.id.value
    }
}

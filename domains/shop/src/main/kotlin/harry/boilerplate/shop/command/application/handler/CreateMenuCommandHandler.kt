package harry.boilerplate.shop.command.application.handler

import harry.boilerplate.common.domain.entity.Money
import harry.boilerplate.shop.command.application.dto.CreateMenuCommand
import harry.boilerplate.shop.command.domain.aggregate.Menu
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository
import harry.boilerplate.shop.command.domain.aggregate.ShopRepository
import harry.boilerplate.shop.command.domain.exception.ShopDomainException
import harry.boilerplate.shop.command.domain.exception.ShopErrorCode
import harry.boilerplate.shop.command.domain.valueObject.ShopId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CreateMenuCommandHandler(
    private val menuRepository: MenuRepository,
    private val shopRepository: ShopRepository
) {
    fun handle(command: CreateMenuCommand): String {
        val shopId = ShopId.of(command.shopId)
        if (!shopRepository.existsById(shopId)) {
            throw ShopDomainException(ShopErrorCode.SHOP_NOT_FOUND, additionalMessage = command.shopId)
        }

        val basePrice = Money.of(command.basePrice)
        val menu = Menu.create(
            shopId = shopId,
            name = command.name,
            description = command.description,
            basePrice = basePrice
        )
        menuRepository.save(menu)
        return menu.id.value
    }
}

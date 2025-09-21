package harry.boilerplate.shop.command.application.handler

import harry.boilerplate.common.domain.entity.Money
import harry.boilerplate.shop.command.application.dto.UpdateShopCommand
import harry.boilerplate.shop.command.domain.aggregate.ShopRepository
import harry.boilerplate.shop.command.domain.exception.ShopDomainException
import harry.boilerplate.shop.command.domain.exception.ShopErrorCode
import harry.boilerplate.shop.command.domain.valueObject.ShopId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UpdateShopCommandHandler(
    private val shopRepository: ShopRepository
) {
    fun handle(command: UpdateShopCommand) {
        val shopId = ShopId.of(command.shopId)
        val shop = shopRepository.findById(shopId)
            ?: throw ShopDomainException(ShopErrorCode.SHOP_NOT_FOUND)

        val newMinOrderAmount = Money.of(command.minOrderAmount)
        shop.changeMinOrderAmount(newMinOrderAmount)
        shopRepository.save(shop)
    }
}

package harry.boilerplate.shop.command.infrastructure.repository

import harry.boilerplate.shop.command.domain.exception.ShopDomainException
import harry.boilerplate.shop.command.domain.exception.ShopErrorCode
import harry.boilerplate.shop.command.domain.valueObject.ShopId

class ShopNotFoundException(shopId: ShopId) : ShopDomainException(
    errorCode = ShopErrorCode.SHOP_NOT_FOUND,
    additionalMessage = shopId.value
)

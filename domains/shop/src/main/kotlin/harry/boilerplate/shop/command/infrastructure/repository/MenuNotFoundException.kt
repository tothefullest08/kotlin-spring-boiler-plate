package harry.boilerplate.shop.command.infrastructure.repository

import harry.boilerplate.shop.command.domain.exception.MenuDomainException
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode
import harry.boilerplate.shop.command.domain.valueObject.MenuId

class MenuNotFoundException(menuId: MenuId) : MenuDomainException(
    errorCode = MenuErrorCode.MENU_NOT_FOUND,
    additionalMessage = menuId.value
)

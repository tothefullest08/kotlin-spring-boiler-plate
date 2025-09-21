package harry.boilerplate.shop.command.application.handler

import harry.boilerplate.shop.command.application.dto.OpenMenuCommand
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository
import harry.boilerplate.shop.command.domain.exception.MenuDomainException
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode
import harry.boilerplate.shop.command.domain.valueObject.MenuId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class OpenMenuCommandHandler(
    private val menuRepository: MenuRepository
) {
    fun handle(command: OpenMenuCommand) {
        val menuId = MenuId.of(command.menuId)
        val menu = menuRepository.findById(menuId)
            ?: throw MenuDomainException(MenuErrorCode.MENU_NOT_FOUND)
        menu.open()
        menuRepository.save(menu)
    }
}

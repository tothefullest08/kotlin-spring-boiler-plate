package harry.boilerplate.shop.command.application.handler

import harry.boilerplate.shop.command.application.dto.AddOptionGroupCommand
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository
import harry.boilerplate.shop.command.domain.exception.MenuDomainException
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode
import harry.boilerplate.shop.command.domain.valueObject.MenuId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class AddOptionGroupCommandHandler(
    private val menuRepository: MenuRepository
) {
    fun handle(command: AddOptionGroupCommand): String {
        val menuId = MenuId.of(command.menuId)
        val menu = menuRepository.findById(menuId)
            ?: throw MenuDomainException(MenuErrorCode.MENU_NOT_FOUND)

        menu.addOptionGroup(command.name, command.isRequired)
        menuRepository.save(menu)

        return menu.optionGroups
            .firstOrNull { it.name == command.name.trim() }
            ?.id
            ?.value
            ?: throw MenuDomainException(MenuErrorCode.OPTION_GROUP_NOT_FOUND)
    }
}

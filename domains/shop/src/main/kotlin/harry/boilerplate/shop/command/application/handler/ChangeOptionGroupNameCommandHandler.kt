package harry.boilerplate.shop.command.application.handler

import harry.boilerplate.shop.command.application.dto.ChangeOptionGroupNameCommand
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository
import harry.boilerplate.shop.command.domain.exception.MenuDomainException
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode
import harry.boilerplate.shop.command.domain.valueObject.MenuId
import harry.boilerplate.shop.command.domain.valueObject.OptionGroupId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ChangeOptionGroupNameCommandHandler(
    private val menuRepository: MenuRepository
) {
    fun handle(command: ChangeOptionGroupNameCommand) {
        val menu = menuRepository.findById(MenuId.of(command.menuId))
            ?: throw MenuDomainException(MenuErrorCode.MENU_NOT_FOUND)
        menu.changeOptionGroupName(OptionGroupId.of(command.optionGroupId), command.newName)
        menuRepository.save(menu)
    }
}

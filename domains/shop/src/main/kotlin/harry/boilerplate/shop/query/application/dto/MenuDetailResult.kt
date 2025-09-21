package harry.boilerplate.shop.query.application.dto

import harry.boilerplate.shop.query.application.readModel.MenuDetailReadModel

data class MenuDetailResult(val menu: MenuDetailReadModel) {
    companion object {
        fun from(menu: MenuDetailReadModel): MenuDetailResult = MenuDetailResult(menu)
    }
}

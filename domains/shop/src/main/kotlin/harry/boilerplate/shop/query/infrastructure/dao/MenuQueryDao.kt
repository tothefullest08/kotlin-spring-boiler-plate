package harry.boilerplate.shop.query.infrastructure.dao

import harry.boilerplate.shop.query.application.readModel.MenuBoardViewModel
import harry.boilerplate.shop.query.application.readModel.MenuDetailReadModel
import harry.boilerplate.shop.query.application.readModel.MenuSummaryReadModel
import java.util.Optional

interface MenuQueryDao {
    fun getMenuBoard(shopId: String): MenuBoardViewModel?
    fun findMenuSummariesByShopId(shopId: String): List<MenuSummaryReadModel>
    fun findOpenMenuSummariesByShopId(shopId: String): List<MenuSummaryReadModel>
    fun findMenuDetail(menuId: String): Optional<MenuDetailReadModel>
    fun existsMenu(menuId: String): Boolean
    fun searchMenusByName(shopId: String, nameKeyword: String): List<MenuSummaryReadModel>
}

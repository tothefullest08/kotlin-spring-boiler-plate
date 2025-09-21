package harry.boilerplate.shop.query.application.dto

import harry.boilerplate.shop.query.application.readModel.MenuBoardViewModel
import harry.boilerplate.shop.query.application.readModel.MenuSummaryReadModel

data class MenuBoardResult(
    val shopId: String,
    val shopName: String,
    val isShopOpen: Boolean,
    val menus: List<MenuSummaryReadModel>,
    val totalMenuCount: Int,
    val openMenuCount: Int
) {
    companion object {
        fun from(viewModel: MenuBoardViewModel): MenuBoardResult = MenuBoardResult(
            shopId = viewModel.shopId,
            shopName = viewModel.shopName,
            isShopOpen = viewModel.isShopOpen,
            menus = viewModel.openMenus,
            totalMenuCount = viewModel.totalMenuCount,
            openMenuCount = viewModel.openMenuCount
        )

        fun empty(shopId: String): MenuBoardResult = MenuBoardResult(
            shopId = shopId,
            shopName = "",
            isShopOpen = false,
            menus = emptyList(),
            totalMenuCount = 0,
            openMenuCount = 0
        )
    }
}

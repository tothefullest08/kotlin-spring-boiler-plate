package harry.boilerplate.shop.query.application.readModel

data class MenuBoardViewModel(
    val shopId: String,
    val shopName: String,
    val isShopOpen: Boolean,
    val openMenus: List<MenuSummaryReadModel>,
    val closedMenus: List<MenuSummaryReadModel>
) {
    val totalMenuCount: Int = openMenus.size + closedMenus.size
    val openMenuCount: Int = openMenus.size
}

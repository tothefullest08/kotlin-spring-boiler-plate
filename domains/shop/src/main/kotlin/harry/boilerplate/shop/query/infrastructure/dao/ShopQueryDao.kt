package harry.boilerplate.shop.query.infrastructure.dao

import harry.boilerplate.shop.query.application.readModel.ShopDetailReadModel
import harry.boilerplate.shop.query.application.readModel.ShopSummaryReadModel
import java.util.Optional

interface ShopQueryDao {
    fun findAllShopSummaries(): List<ShopSummaryReadModel>
    fun findOpenShopSummaries(): List<ShopSummaryReadModel>
    fun findShopDetail(shopId: String): Optional<ShopDetailReadModel>
    fun existsShop(shopId: String): Boolean
    fun searchShopsByName(nameKeyword: String): List<ShopSummaryReadModel>
}

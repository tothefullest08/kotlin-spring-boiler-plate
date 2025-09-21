package harry.boilerplate.shop.query.application.handler

import harry.boilerplate.shop.query.application.dto.ShopInfoQuery
import harry.boilerplate.shop.query.application.dto.ShopInfoResult
import harry.boilerplate.shop.query.infrastructure.dao.ShopQueryDao
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ShopInfoQueryHandler(
    private val shopQueryDao: ShopQueryDao
) {
    fun handle(query: ShopInfoQuery): ShopInfoResult {
        val shop = shopQueryDao.findShopDetail(query.shopId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 가게입니다: ${query.shopId}") }
        return ShopInfoResult.from(shop)
    }
}

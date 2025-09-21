package harry.boilerplate.shop.command.domain.aggregate

import harry.boilerplate.shop.command.domain.valueObject.ShopId

interface ShopRepository {
    fun save(shop: Shop)
    fun findById(shopId: ShopId): Shop?
    fun existsById(shopId: ShopId): Boolean
    fun delete(shop: Shop)
    fun deleteById(shopId: ShopId)
}

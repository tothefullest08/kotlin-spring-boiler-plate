package harry.boilerplate.shop.command.domain.aggregate

import harry.boilerplate.shop.command.domain.valueObject.MenuId
import harry.boilerplate.shop.command.domain.valueObject.ShopId

interface MenuRepository {
    fun save(menu: Menu)
    fun findById(menuId: MenuId): Menu?
    fun findByShopId(shopId: ShopId): List<Menu>
    fun existsById(menuId: MenuId): Boolean
    fun delete(menu: Menu)
    fun deleteById(menuId: MenuId)
    fun deleteByShopId(shopId: ShopId)
}

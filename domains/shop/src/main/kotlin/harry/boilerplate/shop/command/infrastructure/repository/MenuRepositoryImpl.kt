package harry.boilerplate.shop.command.infrastructure.repository

import harry.boilerplate.shop.command.domain.aggregate.Menu
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository
import harry.boilerplate.shop.command.domain.valueObject.MenuId
import harry.boilerplate.shop.command.domain.valueObject.ShopId
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class MenuRepositoryImpl(
    private val menuJpaRepository: MenuJpaRepository
) : MenuRepository {

    override fun save(menu: Menu) {
        requireNotNull(menu) { "Menu cannot be null" }
        menuJpaRepository.save(menu)
    }

    override fun findById(menuId: MenuId): Menu? {
        return menuJpaRepository.findById(menuId.value)
            .orElseThrow { MenuNotFoundException(menuId) }
    }

    override fun findByShopId(shopId: ShopId): List<Menu> =
        menuJpaRepository.findAllByShopIdValueOrderByCreatedAtAsc(shopId.value)

    override fun existsById(menuId: MenuId): Boolean {
        return menuJpaRepository.existsById(menuId.value)
    }

    override fun delete(menu: Menu) {
        requireNotNull(menu) { "Menu cannot be null" }
        if (menuJpaRepository.existsById(menu.id.value)) {
            menuJpaRepository.delete(menu)
        }
    }

    override fun deleteById(menuId: MenuId) {
        if (menuJpaRepository.existsById(menuId.value)) {
            menuJpaRepository.deleteById(menuId.value)
        }
    }

    override fun deleteByShopId(shopId: ShopId) {
        menuJpaRepository.deleteByShopIdValue(shopId.value)
    }
}

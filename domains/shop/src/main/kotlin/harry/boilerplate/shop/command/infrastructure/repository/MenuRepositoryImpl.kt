package harry.boilerplate.shop.command.infrastructure.repository

import harry.boilerplate.shop.command.domain.aggregate.Menu
import harry.boilerplate.shop.command.domain.aggregate.MenuRepository
import harry.boilerplate.shop.command.domain.valueObject.MenuId
import harry.boilerplate.shop.command.domain.valueObject.ShopId
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class MenuRepositoryImpl(
    @PersistenceContext private val entityManager: EntityManager
) : MenuRepository {

    override fun save(menu: Menu) {
        requireNotNull(menu) { "Menu cannot be null" }
        if (existsById(menu.id)) {
            entityManager.merge(menu)
        } else {
            entityManager.persist(menu)
        }
        entityManager.flush()
    }

    override fun findById(menuId: MenuId): Menu? {
        return try {
            entityManager.createQuery(
                "SELECT m FROM Menu m WHERE m.idValue = :id",
                Menu::class.java
            ).setParameter("id", menuId.value)
                .singleResult
        } catch (_: NoResultException) {
            throw MenuNotFoundException(menuId)
        }
    }

    override fun findByShopId(shopId: ShopId): List<Menu> = entityManager.createQuery(
        "SELECT m FROM Menu m WHERE m.shopIdValue = :shopId ORDER BY m.createdAt",
        Menu::class.java
    ).setParameter("shopId", shopId.value)
        .resultList

    override fun existsById(menuId: MenuId): Boolean {
        val count = entityManager.createQuery(
            "SELECT COUNT(m) FROM Menu m WHERE m.idValue = :id",
            java.lang.Long::class.java
        ).setParameter("id", menuId.value)
            .singleResult
        return count > 0
    }

    override fun delete(menu: Menu) {
        requireNotNull(menu) { "Menu cannot be null" }
        val managed = entityManager.find(Menu::class.java, menu.id.value)
        if (managed != null) {
            entityManager.remove(managed)
            entityManager.flush()
        }
    }

    override fun deleteById(menuId: MenuId) {
        val menu = entityManager.find(Menu::class.java, menuId.value)
        if (menu != null) {
            entityManager.remove(menu)
            entityManager.flush()
        }
    }

    override fun deleteByShopId(shopId: ShopId) {
        entityManager.createQuery("DELETE FROM Menu m WHERE m.shopIdValue = :shopId")
            .setParameter("shopId", shopId.value)
            .executeUpdate()
        entityManager.flush()
    }
}

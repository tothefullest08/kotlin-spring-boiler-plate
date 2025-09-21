package harry.boilerplate.shop.command.infrastructure.repository

import harry.boilerplate.shop.command.domain.aggregate.Shop
import harry.boilerplate.shop.command.domain.aggregate.ShopRepository
import harry.boilerplate.shop.command.domain.valueObject.ShopId
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class ShopRepositoryImpl(
    @PersistenceContext private val entityManager: EntityManager
) : ShopRepository {

    override fun save(shop: Shop) {
        requireNotNull(shop) { "Shop cannot be null" }
        if (existsById(shop.id)) {
            entityManager.merge(shop)
        } else {
            entityManager.persist(shop)
        }
        entityManager.flush()
    }

    override fun findById(shopId: ShopId): Shop? {
        return try {
            entityManager.createQuery(
                "SELECT s FROM Shop s WHERE s.idValue = :id",
                Shop::class.java
            ).setParameter("id", shopId.value)
                .singleResult
        } catch (_: NoResultException) {
            throw ShopNotFoundException(shopId)
        }
    }

    override fun existsById(shopId: ShopId): Boolean {
        val count = entityManager.createQuery(
            "SELECT COUNT(s) FROM Shop s WHERE s.idValue = :id",
            java.lang.Long::class.java
        ).setParameter("id", shopId.value)
            .singleResult
        return count > 0
    }

    override fun delete(shop: Shop) {
        val managed = entityManager.find(Shop::class.java, shop.id.value)
        if (managed != null) {
            entityManager.remove(managed)
            entityManager.flush()
        }
    }

    override fun deleteById(shopId: ShopId) {
        val shop = entityManager.find(Shop::class.java, shopId.value)
        if (shop != null) {
            entityManager.remove(shop)
            entityManager.flush()
        }
    }
}

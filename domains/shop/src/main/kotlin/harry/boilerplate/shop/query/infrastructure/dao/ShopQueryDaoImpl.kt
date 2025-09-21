package harry.boilerplate.shop.query.infrastructure.dao

import harry.boilerplate.shop.query.application.readModel.ShopDetailReadModel
import harry.boilerplate.shop.query.application.readModel.ShopSummaryReadModel
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.jpa.AvailableHints
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalTime
import java.util.Optional

@Repository
@Transactional(readOnly = true)
class ShopQueryDaoImpl(
    @PersistenceContext private val entityManager: EntityManager
) : ShopQueryDao {

    override fun findAllShopSummaries(): List<ShopSummaryReadModel> {
        val jpql = """
            SELECT s.id, s.name, s.minOrderAmount, s.businessHours.openTime, s.businessHours.closeTime
            FROM Shop s
            ORDER BY s.createdAt DESC
        """

        return entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .setHint(AvailableHints.HINT_CACHEABLE, true)
            .resultList
            .map { row ->
                val openTime = row[3] as? LocalTime
                val closeTime = row[4] as? LocalTime
                ShopSummaryReadModel(
                    id = row[0] as String,
                    name = row[1] as String,
                    minOrderAmount = row[2] as BigDecimal,
                    openTime = openTime,
                    closeTime = closeTime,
                    isOpen = isCurrentlyOpen(openTime, closeTime)
                )
            }
    }

    override fun findOpenShopSummaries(): List<ShopSummaryReadModel> {
        val jpql = """
            SELECT s.id, s.name, s.minOrderAmount, s.businessHours.openTime, s.businessHours.closeTime
            FROM Shop s
            WHERE s.businessHours.openTime IS NOT NULL AND s.businessHours.closeTime IS NOT NULL
            ORDER BY s.createdAt DESC
        """

        return entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .setHint(AvailableHints.HINT_CACHEABLE, true)
            .resultList
            .mapNotNull { row ->
                val openTime = row[3] as? LocalTime
                val closeTime = row[4] as? LocalTime
                if (!isCurrentlyOpen(openTime, closeTime)) {
                    null
                } else {
                    ShopSummaryReadModel(
                        id = row[0] as String,
                        name = row[1] as String,
                        minOrderAmount = row[2] as BigDecimal,
                        openTime = openTime,
                        closeTime = closeTime,
                        isOpen = true
                    )
                }
            }
    }

    override fun findShopDetail(shopId: String): Optional<ShopDetailReadModel> {
        val jpql = """
            SELECT s.id, s.name, s.minOrderAmount, s.businessHours.openTime, s.businessHours.closeTime,
                   s.createdAt, s.updatedAt
            FROM Shop s
            WHERE s.id = :shopId
        """

        val results = entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .resultList

        if (results.isEmpty()) {
            return Optional.empty()
        }

        val row = results.first()
        val openTime = row[3] as? LocalTime
        val closeTime = row[4] as? LocalTime
        val detail = ShopDetailReadModel(
            id = row[0] as String,
            name = row[1] as String,
            minOrderAmount = row[2] as BigDecimal,
            openTime = openTime,
            closeTime = closeTime,
            isOpen = isCurrentlyOpen(openTime, closeTime),
            createdAt = row[5] as java.time.Instant,
            updatedAt = row[6] as java.time.Instant
        )
        return Optional.of(detail)
    }

    override fun existsShop(shopId: String): Boolean {
        val jpql = "SELECT COUNT(s) FROM Shop s WHERE s.id = :shopId"
        val count = entityManager.createQuery(jpql, java.lang.Long::class.java)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .singleResult
        return count > 0
    }

    override fun searchShopsByName(nameKeyword: String): List<ShopSummaryReadModel> {
        val jpql = """
            SELECT s.id, s.name, s.minOrderAmount, s.businessHours.openTime, s.businessHours.closeTime
            FROM Shop s
            WHERE s.name LIKE :nameKeyword
            ORDER BY s.name
        """

        return entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setParameter("nameKeyword", "%$nameKeyword%")
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .resultList
            .map { row ->
                val openTime = row[3] as? LocalTime
                val closeTime = row[4] as? LocalTime
                ShopSummaryReadModel(
                    id = row[0] as String,
                    name = row[1] as String,
                    minOrderAmount = row[2] as BigDecimal,
                    openTime = openTime,
                    closeTime = closeTime,
                    isOpen = isCurrentlyOpen(openTime, closeTime)
                )
            }
    }

    private fun isCurrentlyOpen(openTime: LocalTime?, closeTime: LocalTime?): Boolean {
        if (openTime == null || closeTime == null) return false
        val currentTime = LocalTime.now()
        return if (openTime.isBefore(closeTime)) {
            !currentTime.isBefore(openTime) && currentTime.isBefore(closeTime)
        } else {
            !currentTime.isBefore(openTime) || currentTime.isBefore(closeTime)
        }
    }
}

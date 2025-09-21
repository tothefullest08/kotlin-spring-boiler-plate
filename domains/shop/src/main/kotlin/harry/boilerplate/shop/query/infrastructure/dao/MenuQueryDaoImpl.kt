package harry.boilerplate.shop.query.infrastructure.dao

import harry.boilerplate.shop.query.application.readModel.MenuBoardViewModel
import harry.boilerplate.shop.query.application.readModel.MenuDetailReadModel
import harry.boilerplate.shop.query.application.readModel.MenuSummaryReadModel
import harry.boilerplate.shop.query.application.readModel.OptionGroupReadModel
import harry.boilerplate.shop.query.application.readModel.OptionReadModel
import harry.boilerplate.shop.query.infrastructure.mapper.MenuReadModelMapper
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
class MenuQueryDaoImpl(
    @PersistenceContext private val entityManager: EntityManager,
    private val mapper: MenuReadModelMapper
) : MenuQueryDao {

    override fun getMenuBoard(shopId: String): MenuBoardViewModel {
        val shopJpql = """
            SELECT s.id, s.name, s.businessHours.openTime, s.businessHours.closeTime
            FROM Shop s
            WHERE s.id = :shopId
        """

        val shopResults = entityManager.createQuery(shopJpql, Array<Any?>::class.java)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .resultList

        if (shopResults.isEmpty()) {
            throw IllegalArgumentException("Shop not found: $shopId")
        }

        val row = shopResults.first()
        val shopName = row[1] as String
        val openTime = row[2] as? LocalTime
        val closeTime = row[3] as? LocalTime
        val shopIsOpen = isCurrentlyOpen(openTime, closeTime)

        val menus = findMenuSummariesByShopId(shopId)
        val openMenus = menus.filter { it.isOpen }
        val closedMenus = menus.filterNot { it.isOpen }

        return MenuBoardViewModel(shopId, shopName, shopIsOpen, openMenus, closedMenus)
    }

    override fun findMenuSummariesByShopId(shopId: String): List<MenuSummaryReadModel> {
        val jpql = """
            SELECT m.id, m.shopId, m.name, m.description, m.basePrice, m.open,
                   (SELECT COUNT(og) FROM OptionGroupEntity og WHERE og.menu.id = m.id)
            FROM Menu m
            WHERE m.shopId = :shopId
            ORDER BY m.createdAt
        """

        return entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .setHint(AvailableHints.HINT_CACHEABLE, true)
            .resultList
            .map { row ->
                MenuSummaryReadModel(
                    id = row[0] as String,
                    shopId = row[1] as String,
                    name = row[2] as String,
                    description = row[3] as? String,
                    basePrice = row[4] as BigDecimal,
                    isOpen = row[5] as Boolean,
                    optionGroupCount = (row[6] as Long).toInt()
                )
            }
    }

    override fun findOpenMenuSummariesByShopId(shopId: String): List<MenuSummaryReadModel> {
        val jpql = """
            SELECT m.id, m.shopId, m.name, m.description, m.basePrice, m.open,
                   (SELECT COUNT(og) FROM OptionGroupEntity og WHERE og.menu.id = m.id)
            FROM Menu m
            WHERE m.shopId = :shopId AND m.open = true
            ORDER BY m.createdAt
        """

        return entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .setHint(AvailableHints.HINT_CACHEABLE, true)
            .resultList
            .map { row ->
                MenuSummaryReadModel(
                    id = row[0] as String,
                    shopId = row[1] as String,
                    name = row[2] as String,
                    description = row[3] as? String,
                    basePrice = row[4] as BigDecimal,
                    isOpen = row[5] as Boolean,
                    optionGroupCount = (row[6] as Long).toInt()
                )
            }
    }

    override fun findMenuDetail(menuId: String): Optional<MenuDetailReadModel> {
        val jpql = """
            SELECT m.id, m.shopId, m.name, m.description, m.basePrice, m.open,
                   m.createdAt, m.updatedAt
            FROM Menu m
            WHERE m.id = :menuId
        """

        val results = entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setParameter("menuId", menuId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .resultList

        if (results.isEmpty()) {
            return Optional.empty()
        }

        val row = results.first()
        val optionGroups = findOptionGroupsByMenuId(menuId)

        val detail = MenuDetailReadModel(
            id = row[0] as String,
            shopId = row[1] as String,
            name = row[2] as String,
            description = row[3] as? String,
            basePrice = row[4] as BigDecimal,
            isOpen = row[5] as Boolean,
            optionGroups = optionGroups,
            createdAt = row[6] as java.time.Instant,
            updatedAt = row[7] as java.time.Instant
        )
        return Optional.of(detail)
    }

    override fun existsMenu(menuId: String): Boolean {
        val jpql = "SELECT COUNT(m) FROM Menu m WHERE m.id = :menuId"
        val count = entityManager.createQuery(jpql, java.lang.Long::class.java)
            .setParameter("menuId", menuId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .singleResult
        return count > 0
    }

    override fun searchMenusByName(shopId: String, nameKeyword: String): List<MenuSummaryReadModel> {
        val jpql = """
            SELECT m.id, m.shopId, m.name, m.description, m.basePrice, m.open,
                   (SELECT COUNT(og) FROM OptionGroupEntity og WHERE og.menu.id = m.id)
            FROM Menu m
            WHERE m.shopId = :shopId AND m.name LIKE :nameKeyword
            ORDER BY m.name
        """

        return entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setParameter("shopId", shopId)
            .setParameter("nameKeyword", "%$nameKeyword%")
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .resultList
            .map { row ->
                MenuSummaryReadModel(
                    id = row[0] as String,
                    shopId = row[1] as String,
                    name = row[2] as String,
                    description = row[3] as? String,
                    basePrice = row[4] as BigDecimal,
                    isOpen = row[5] as Boolean,
                    optionGroupCount = (row[6] as Long).toInt()
                )
            }
    }

    private fun findOptionGroupsByMenuId(menuId: String): List<OptionGroupReadModel> {
        val jpql = """
            SELECT og.id, og.name, og.required
            FROM OptionGroupEntity og
            WHERE og.menu.id = :menuId
            ORDER BY og.createdAt
        """

        return entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setParameter("menuId", menuId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .resultList
            .map { row ->
                val optionGroupId = row[0] as String
                OptionGroupReadModel(
                    id = optionGroupId,
                    name = row[1] as String,
                    required = row[2] as Boolean,
                    options = findOptionsByOptionGroupId(optionGroupId)
                )
            }
    }

    private fun findOptionsByOptionGroupId(optionGroupId: String): List<OptionReadModel> {
        val jpql = """
            SELECT o.name, o.price
            FROM OptionEntity o
            WHERE o.optionGroup.id = :optionGroupId
            ORDER BY o.createdAt
        """

        return entityManager.createQuery(jpql, Array<Any?>::class.java)
            .setParameter("optionGroupId", optionGroupId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .resultList
            .map { row ->
                OptionReadModel(
                    name = row[0] as String,
                    price = row[1] as BigDecimal
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

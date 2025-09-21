package harry.boilerplate.shop.command.infrastructure.repository

import harry.boilerplate.shop.command.domain.aggregate.Menu
import org.springframework.data.jpa.repository.JpaRepository

interface MenuJpaRepository : JpaRepository<Menu, String> {
    fun findAllByShopIdValueOrderByCreatedAtAsc(shopIdValue: String): List<Menu>
    fun deleteByShopIdValue(shopIdValue: String): Long
}

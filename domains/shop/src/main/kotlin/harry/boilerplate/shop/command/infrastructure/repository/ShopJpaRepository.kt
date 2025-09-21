package harry.boilerplate.shop.command.infrastructure.repository

import harry.boilerplate.shop.command.domain.aggregate.Shop
import org.springframework.data.jpa.repository.JpaRepository

interface ShopJpaRepository : JpaRepository<Shop, String>

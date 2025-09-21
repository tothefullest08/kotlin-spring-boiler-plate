package harry.boilerplate.shop.query.application.readModel

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalTime

data class ShopDetailReadModel(
    val id: String,
    val name: String,
    val minOrderAmount: BigDecimal,
    val openTime: LocalTime?,
    val closeTime: LocalTime?,
    val isOpen: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

package harry.boilerplate.shop.query.application.readModel

import java.math.BigDecimal
import java.time.Instant

data class MenuDetailReadModel(
    val id: String,
    val shopId: String,
    val name: String,
    val description: String?,
    val basePrice: BigDecimal,
    val isOpen: Boolean,
    val optionGroups: List<OptionGroupReadModel>,
    val createdAt: Instant,
    val updatedAt: Instant
)

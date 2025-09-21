package harry.boilerplate.shop.query.application.readModel

import java.math.BigDecimal

data class MenuSummaryReadModel(
    val id: String,
    val shopId: String,
    val name: String,
    val description: String?,
    val basePrice: BigDecimal,
    val isOpen: Boolean,
    val optionGroupCount: Int
)

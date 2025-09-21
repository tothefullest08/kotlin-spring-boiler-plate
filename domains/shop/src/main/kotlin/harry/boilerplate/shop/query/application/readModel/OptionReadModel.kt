package harry.boilerplate.shop.query.application.readModel

import java.math.BigDecimal

data class OptionReadModel(
    val name: String,
    val price: BigDecimal
) {
    val isPaid: Boolean = price > BigDecimal.ZERO
}

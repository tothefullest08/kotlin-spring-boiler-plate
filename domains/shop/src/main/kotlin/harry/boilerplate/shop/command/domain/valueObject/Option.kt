package harry.boilerplate.shop.command.domain.valueObject

import harry.boilerplate.common.domain.entity.Money
import harry.boilerplate.common.domain.entity.ValueObject
import harry.boilerplate.shop.command.domain.exception.MenuDomainException
import harry.boilerplate.shop.command.domain.exception.MenuErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import java.math.BigDecimal

@Embeddable
class Option private constructor(
    @Column(name = "option_name", nullable = false)
    val name: String,
    @Embedded
    val price: Money
) : ValueObject() {
    override val equalityComponents: Array<out Any?> = arrayOf(name, price)

    fun changeName(newName: String): Option {
        if (newName.isBlank()) {
            throw MenuDomainException(MenuErrorCode.NEW_OPTION_NAME_REQUIRED)
        }
        return of(newName, price)
    }

    fun changePrice(newPrice: Money?): Option {
        if (newPrice == null) {
            throw MenuDomainException(MenuErrorCode.NEW_OPTION_PRICE_REQUIRED)
        }
        return of(name, newPrice)
    }

    fun isPaid(): Boolean = price.amount > BigDecimal.ZERO

    fun isFree(): Boolean = !isPaid()

    companion object {
        fun of(name: String?, price: Money?): Option {
            if (name == null || name.isBlank()) {
                throw MenuDomainException(MenuErrorCode.CURRENT_OPTION_NAME_REQUIRED)
            }
            if (price == null) {
                throw MenuDomainException(MenuErrorCode.CURRENT_OPTION_PRICE_REQUIRED)
            }
            if (price.amount < BigDecimal.ZERO) {
                throw MenuDomainException(MenuErrorCode.INVALID_BASE_PRICE)
            }
            return Option(name.trim(), price)
        }
    }

    @Suppress("unused")
    private constructor() : this("", Money.zero())
}

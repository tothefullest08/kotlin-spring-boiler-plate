package harry.boilerplate.common.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

/**
 * 금액을 표현하는 값 객체
 * 불변성과 정확한 계산을 보장
 */
@Embeddable
class Money private constructor(
    @Column(name = "amount", precision = 10, scale = 2)
    val amount: BigDecimal,
    @Column(name = "currency", length = 3, nullable = false)
    val currency: Currency
) : ValueObject(), Serializable {

    init {
        require(amount.scale() == 2) { "Amount must have scale 2" }
    }

    override val equalityComponents: Array<Any?> = arrayOf(amount, currency)

    operator fun plus(other: Money): Money {
        validateSameCurrency(other)
        return of(amount.add(other.amount), currency)
    }

    operator fun minus(other: Money): Money {
        validateSameCurrency(other)
        return of(amount.subtract(other.amount), currency)
    }

    operator fun times(multiplier: Int): Money = of(amount.multiply(BigDecimal.valueOf(multiplier.toLong())), currency)

    operator fun times(multiplier: BigDecimal): Money = of(amount.multiply(multiplier), currency)

    fun isGreaterThan(other: Money): Boolean {
        validateSameCurrency(other)
        return amount > other.amount
    }

    fun isGreaterThanOrEqual(other: Money): Boolean {
        validateSameCurrency(other)
        return amount >= other.amount
    }

    fun isLessThan(other: Money): Boolean {
        validateSameCurrency(other)
        return amount < other.amount
    }

    fun isZero(): Boolean = amount.compareTo(ZERO) == 0

    fun isPositive(): Boolean = amount.compareTo(ZERO) > 0

    private fun validateSameCurrency(other: Money) {
        require(currency == other.currency) { "Cannot operate on different currencies" }
    }

    override fun toString(): String = "$amount ${currency.currencyCode}"

    companion object {
        private val DEFAULT_CURRENCY: Currency = Currency.getInstance("KRW")
        private val ZERO: BigDecimal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)

        @JvmStatic
        fun zero(currency: Currency = DEFAULT_CURRENCY): Money = of(BigDecimal.ZERO, currency)

        @JvmStatic
        fun of(amount: BigDecimal, currency: Currency = DEFAULT_CURRENCY): Money {
            val scaled = amount.setScale(2, RoundingMode.HALF_UP)
            return Money(scaled, currency)
        }

        @JvmStatic
        fun of(amount: String, currency: Currency = DEFAULT_CURRENCY): Money = of(BigDecimal(amount), currency)

        @JvmStatic
        fun of(amount: Double, currency: Currency = DEFAULT_CURRENCY): Money = of(BigDecimal.valueOf(amount), currency)
    }

    @Suppress("unused")
    private constructor() : this(ZERO, DEFAULT_CURRENCY)
}

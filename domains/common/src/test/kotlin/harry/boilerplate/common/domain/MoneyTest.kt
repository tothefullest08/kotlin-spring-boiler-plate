package harry.boilerplate.common.domain

import harry.boilerplate.common.domain.entity.Money
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Currency

class MoneyTest {

    @Test
    fun `Money 생성`() {
        val money = Money.of("1000")

        assertThat(money.amount).isEqualTo(BigDecimal("1000.00"))
        assertThat(money.currency).isEqualTo(Currency.getInstance("KRW"))
    }

    @Test
    fun `Money 덧셈`() {
        val result = Money.of("1000") + Money.of("500")

        assertThat(result.amount).isEqualTo(BigDecimal("1500.00"))
    }

    @Test
    fun `Money 뺄셈`() {
        val result = Money.of("1000") - Money.of("300")

        assertThat(result.amount).isEqualTo(BigDecimal("700.00"))
    }

    @Test
    fun `Money 곱셈`() {
        val result = Money.of("100") * 3

        assertThat(result.amount).isEqualTo(BigDecimal("300.00"))
    }

    @Test
    fun `Money 비교`() {
        val money1 = Money.of("1000")
        val money2 = Money.of("500")
        val money3 = Money.of("1000")

        assertThat(money1.isGreaterThan(money2)).isTrue()
        assertThat(money2.isLessThan(money1)).isTrue()
        assertThat(money1.isGreaterThanOrEqual(money3)).isTrue()
    }

    @Test
    fun `Money 동등성`() {
        val money1 = Money.of("1000")
        val money2 = Money.of("1000")
        val money3 = Money.of("500")

        assertThat(money1).isEqualTo(money2)
        assertThat(money1).isNotEqualTo(money3)
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode())
    }

    @Test
    fun `Money zero`() {
        val zero = Money.zero()

        assertThat(zero.isZero()).isTrue()
        assertThat(zero.isPositive()).isFalse()
    }

    @Test
    fun `다른 통화 연산시 예외`() {
        val krw = Money.of(BigDecimal.valueOf(1000), Currency.getInstance("KRW"))
        val usd = Money.of(BigDecimal.valueOf(1000), Currency.getInstance("USD"))

        assertThatThrownBy { krw + usd }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Cannot operate on different currencies")
    }
}

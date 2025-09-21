package harry.boilerplate.shop.command.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateShopCommand(
    @field:NotBlank(message = "가게 이름은 필수입니다")
    @field:Size(max = 255, message = "가게 이름은 255자 이하여야 합니다")
    val name: String,
    @field:NotNull(message = "최소 주문금액은 필수입니다")
    @field:Positive(message = "최소 주문금액은 양수여야 합니다")
    val minOrderAmount: BigDecimal
)

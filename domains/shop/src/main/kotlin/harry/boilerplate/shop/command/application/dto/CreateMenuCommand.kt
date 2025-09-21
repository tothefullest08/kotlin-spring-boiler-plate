package harry.boilerplate.shop.command.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateMenuCommand(
    @field:NotBlank(message = "가게 ID는 필수입니다")
    val shopId: String,
    @field:NotBlank(message = "메뉴 이름은 필수입니다")
    @field:Size(max = 255, message = "메뉴 이름은 255자 이하여야 합니다")
    val name: String,
    @field:Size(max = 1000, message = "메뉴 설명은 1000자 이하여야 합니다")
    val description: String?,
    @field:NotNull(message = "기본 가격은 필수입니다")
    @field:Positive(message = "기본 가격은 양수여야 합니다")
    val basePrice: BigDecimal
)

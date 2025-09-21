package harry.boilerplate.shop.command.presentation.dto

import harry.boilerplate.shop.command.application.dto.CreateShopCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Schema(description = "가게 생성 요청")
data class CreateShopRequest(
    @field:NotBlank(message = "가게 이름은 필수입니다")
    @field:Size(max = 255, message = "가게 이름은 255자 이하여야 합니다")
    @Schema(description = "가게 이름", example = "맛있는 삼겹살집", required = true)
    var name: String? = null,
    @field:NotNull(message = "최소 주문금액은 필수입니다")
    @field:Positive(message = "최소 주문금액은 양수여야 합니다")
    @Schema(description = "최소 주문금액", example = "15000", required = true)
    var minOrderAmount: BigDecimal? = null
) {
    fun toCommand(): CreateShopCommand = CreateShopCommand(
        name = requireNotNull(name),
        minOrderAmount = requireNotNull(minOrderAmount)
    )
}

package harry.boilerplate.shop.command.presentation.dto

import harry.boilerplate.shop.command.application.dto.CreateMenuCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Schema(description = "메뉴 생성 요청")
data class CreateMenuRequest(
    @field:NotBlank(message = "메뉴 이름은 필수입니다")
    @field:Size(max = 255, message = "메뉴 이름은 255자 이하여야 합니다")
    @Schema(description = "메뉴 이름", example = "삼겹살", required = true)
    var name: String? = null,
    @field:Size(max = 1000, message = "메뉴 설명은 1000자 이하여야 합니다")
    @Schema(description = "메뉴 설명", example = "맛있는 삼겹살입니다")
    var description: String? = null,
    @field:NotNull(message = "기본 가격은 필수입니다")
    @field:Positive(message = "기본 가격은 양수여야 합니다")
    @Schema(description = "기본 가격", example = "18000", required = true)
    var basePrice: BigDecimal? = null
) {
    fun toCommand(shopId: String): CreateMenuCommand = CreateMenuCommand(
        shopId = shopId,
        name = requireNotNull(name),
        description = description,
        basePrice = requireNotNull(basePrice)
    )
}

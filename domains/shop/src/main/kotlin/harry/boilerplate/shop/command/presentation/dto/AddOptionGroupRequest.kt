package harry.boilerplate.shop.command.presentation.dto

import harry.boilerplate.shop.command.application.dto.AddOptionGroupCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "옵션 그룹 추가 요청")
data class AddOptionGroupRequest(
    @field:NotBlank(message = "옵션 그룹 이름은 필수입니다")
    @field:Size(max = 255, message = "옵션 그룹 이름은 255자 이하여야 합니다")
    @Schema(description = "옵션 그룹 이름", example = "고기 선택", required = true)
    var name: String? = null,
    @field:NotNull(message = "필수 여부는 필수입니다")
    @Schema(description = "필수 여부", example = "true", required = true)
    var isRequired: Boolean? = null
) {
    fun toCommand(menuId: String): AddOptionGroupCommand = AddOptionGroupCommand(
        menuId = menuId,
        name = requireNotNull(name),
        isRequired = requireNotNull(isRequired)
    )
}

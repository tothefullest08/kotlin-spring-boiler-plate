package harry.boilerplate.shop.command.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class AddOptionGroupCommand(
    @field:NotBlank(message = "메뉴 ID는 필수입니다")
    val menuId: String,
    @field:NotBlank(message = "옵션 그룹 이름은 필수입니다")
    @field:Size(max = 255, message = "옵션 그룹 이름은 255자 이하여야 합니다")
    val name: String,
    @field:NotNull(message = "필수 여부는 필수입니다")
    val isRequired: Boolean
)

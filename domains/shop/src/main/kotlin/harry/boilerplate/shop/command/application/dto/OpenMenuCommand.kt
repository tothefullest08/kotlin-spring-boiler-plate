package harry.boilerplate.shop.command.application.dto

import jakarta.validation.constraints.NotBlank

data class OpenMenuCommand(
    @field:NotBlank(message = "메뉴 ID는 필수입니다")
    val menuId: String
)

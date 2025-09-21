package harry.boilerplate.shop.query.application.dto

import jakarta.validation.constraints.NotBlank

data class MenuDetailQuery(
    @field:NotBlank(message = "메뉴 ID는 필수입니다")
    val menuId: String
)

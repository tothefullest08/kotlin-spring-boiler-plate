package harry.boilerplate.shop.query.application.dto

import jakarta.validation.constraints.NotBlank

data class MenuBoardQuery(
    @field:NotBlank(message = "가게 ID는 필수입니다")
    val shopId: String
)

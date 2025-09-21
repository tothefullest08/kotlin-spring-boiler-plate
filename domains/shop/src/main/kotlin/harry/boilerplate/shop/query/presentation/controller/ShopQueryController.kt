package harry.boilerplate.shop.query.presentation.controller

import harry.boilerplate.shop.query.application.dto.ShopInfoQuery
import harry.boilerplate.shop.query.application.dto.ShopInfoResult
import harry.boilerplate.shop.query.application.handler.ShopInfoQueryHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/shops")
@Tag(name = "Shop Query API", description = "가게 관련 조회 API")
class ShopQueryController(
    private val shopInfoQueryHandler: ShopInfoQueryHandler
) {

    @GetMapping("/{shopId}")
    @Operation(summary = "가게 정보 조회", description = "가게의 상세 정보를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "가게 정보 조회 성공"),
            ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류")
        ]
    )
    fun getShopInfo(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable shopId: String
    ): ResponseEntity<ShopInfoResult> {
        val result = shopInfoQueryHandler.handle(ShopInfoQuery(shopId))
        return ResponseEntity.ok(result)
    }
}

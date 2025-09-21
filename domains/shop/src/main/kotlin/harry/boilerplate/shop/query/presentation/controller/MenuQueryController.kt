package harry.boilerplate.shop.query.presentation.controller

import harry.boilerplate.shop.query.application.dto.MenuBoardQuery
import harry.boilerplate.shop.query.application.dto.MenuBoardResult
import harry.boilerplate.shop.query.application.dto.MenuDetailQuery
import harry.boilerplate.shop.query.application.dto.MenuDetailResult
import harry.boilerplate.shop.query.application.handler.MenuBoardQueryHandler
import harry.boilerplate.shop.query.application.handler.MenuDetailQueryHandler
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
@RequestMapping("/api/shops/{shopId}/menus")
@Tag(name = "Menu Query API", description = "메뉴 관련 조회 API")
class MenuQueryController(
    private val menuBoardQueryHandler: MenuBoardQueryHandler,
    private val menuDetailQueryHandler: MenuDetailQueryHandler
) {

    @GetMapping
    @Operation(summary = "메뉴보드 조회", description = "가게의 메뉴보드를 조회합니다 (공개된 메뉴만)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "메뉴보드 조회 성공"),
            ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류")
        ]
    )
    fun getMenuBoard(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable shopId: String
    ): ResponseEntity<MenuBoardResult?> {
        val result = menuBoardQueryHandler.handle(MenuBoardQuery(shopId))
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{menuId}")
    @Operation(summary = "메뉴 상세 조회", description = "특정 메뉴의 상세 정보를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "메뉴 상세 조회 성공"),
            ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류")
        ]
    )
    fun getMenuDetail(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable shopId: String,
        @Parameter(description = "메뉴 ID", required = true)
        @PathVariable menuId: String
    ): ResponseEntity<MenuDetailResult> {
        val result = menuDetailQueryHandler.handle(MenuDetailQuery(menuId))
        return ResponseEntity.ok(result)
    }
}

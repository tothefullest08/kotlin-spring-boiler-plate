package harry.boilerplate.shop.command.presentation.controller

import harry.boilerplate.common.response.CommandResultResponse
import harry.boilerplate.shop.command.application.dto.OpenMenuCommand
import harry.boilerplate.shop.command.application.handler.AddOptionGroupCommandHandler
import harry.boilerplate.shop.command.application.handler.CreateMenuCommandHandler
import harry.boilerplate.shop.command.application.handler.OpenMenuCommandHandler
import harry.boilerplate.shop.command.presentation.dto.AddOptionGroupRequest
import harry.boilerplate.shop.command.presentation.dto.CreateMenuRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/shops/{shopId}/menus")
@Tag(name = "Menu Command API", description = "메뉴 관련 명령 API")
class MenuCommandController(
    private val createMenuCommandHandler: CreateMenuCommandHandler,
    private val openMenuCommandHandler: OpenMenuCommandHandler,
    private val addOptionGroupCommandHandler: AddOptionGroupCommandHandler
) {

    @PostMapping
    @Operation(summary = "메뉴 생성", description = "가게에 새로운 메뉴를 생성합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "메뉴 생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류")
        ]
    )
    fun createMenu(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable shopId: String,
        @Valid @RequestBody request: CreateMenuRequest
    ): ResponseEntity<CommandResultResponse> {
        val menuId = createMenuCommandHandler.handle(request.toCommand(shopId))
        val response = CommandResultResponse.success("메뉴가 성공적으로 생성되었습니다", menuId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{menuId}/open")
    @Operation(summary = "메뉴 공개", description = "메뉴를 공개 상태로 변경합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "메뉴 공개 성공"),
            ApiResponse(responseCode = "400", description = "메뉴 공개 조건 미충족"),
            ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류")
        ]
    )
    fun openMenu(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable shopId: String,
        @Parameter(description = "메뉴 ID", required = true)
        @PathVariable menuId: String
    ): ResponseEntity<CommandResultResponse> {
        openMenuCommandHandler.handle(OpenMenuCommand(menuId))
        val response = CommandResultResponse.success("메뉴가 성공적으로 공개되었습니다")
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{menuId}/option-groups")
    @Operation(summary = "옵션 그룹 추가", description = "메뉴에 새로운 옵션 그룹을 추가합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "옵션 그룹 추가 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 중복된 옵션 그룹 이름"),
            ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류")
        ]
    )
    fun addOptionGroup(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable shopId: String,
        @Parameter(description = "메뉴 ID", required = true)
        @PathVariable menuId: String,
        @Valid @RequestBody request: AddOptionGroupRequest
    ): ResponseEntity<CommandResultResponse> {
        val optionGroupId = addOptionGroupCommandHandler.handle(request.toCommand(menuId))
        val response = CommandResultResponse.success("옵션 그룹이 성공적으로 추가되었습니다", optionGroupId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}

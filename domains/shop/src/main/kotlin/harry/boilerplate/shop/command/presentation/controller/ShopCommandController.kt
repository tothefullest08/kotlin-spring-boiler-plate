package harry.boilerplate.shop.command.presentation.controller

import harry.boilerplate.common.response.CommandResultResponse
import harry.boilerplate.shop.command.application.handler.CreateShopCommandHandler
import harry.boilerplate.shop.command.application.handler.UpdateShopCommandHandler
import harry.boilerplate.shop.command.presentation.dto.CreateShopRequest
import harry.boilerplate.shop.command.presentation.dto.UpdateShopRequest
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
@RequestMapping("/api/shops")
@Tag(name = "Shop Command API", description = "가게 관련 명령 API")
class ShopCommandController(
    private val createShopCommandHandler: CreateShopCommandHandler,
    private val updateShopCommandHandler: UpdateShopCommandHandler
) {

    @PostMapping
    @Operation(summary = "가게 생성", description = "새로운 가게를 생성합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "가게 생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류")
        ]
    )
    fun createShop(@Valid @RequestBody request: CreateShopRequest): ResponseEntity<CommandResultResponse> {
        val shopId = createShopCommandHandler.handle(request.toCommand())
        val response = CommandResultResponse.success("가게가 성공적으로 생성되었습니다", shopId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{shopId}")
    @Operation(summary = "가게 정보 수정", description = "기존 가게의 정보를 수정합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "가게 정보 수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음"),
            ApiResponse(responseCode = "500", description = "서버 내부 오류")
        ]
    )
    fun updateShop(
        @Parameter(description = "가게 ID", required = true)
        @PathVariable shopId: String,
        @Valid @RequestBody request: UpdateShopRequest
    ): ResponseEntity<CommandResultResponse> {
        updateShopCommandHandler.handle(request.toCommand(shopId))
        val response = CommandResultResponse.success("가게 정보가 성공적으로 수정되었습니다")
        return ResponseEntity.ok(response)
    }
}

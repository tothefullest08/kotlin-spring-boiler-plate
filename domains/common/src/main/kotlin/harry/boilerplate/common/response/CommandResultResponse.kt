package harry.boilerplate.common.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Command 요청 결과 응답")
data class CommandResultResponse(
    @Schema(description = "처리 상태", example = "SUCCESS")
    val status: String,
    @Schema(description = "결과 메시지", example = "요청이 성공적으로 처리되었습니다.")
    val message: String,
    @Schema(description = "생성/수정된 리소스의 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", nullable = true)
    val resourceId: String?,
    @Schema(description = "응답 생성 시각 (ISO-8601)", example = "2025-09-20T11:20:30Z")
    val timestamp: String = Instant.now().toString()
) {
    companion object {
        fun success(message: String, resourceId: String? = null): CommandResultResponse =
            CommandResultResponse(
                status = "SUCCESS",
                message = message,
                resourceId = resourceId
            )
    }
}

package harry.boilerplate.common.exception

enum class CommonSystemErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
    INTERNAL_SERVER_ERROR("COMMON-SYSTEM-001", "Internal server error"),
    INVALID_REQUEST("COMMON-SYSTEM-002", "Invalid request"),
    RESOURCE_NOT_FOUND("COMMON-SYSTEM-003", "Resource not found"),
    UNAUTHORIZED("COMMON-SYSTEM-004", "Unauthorized"),
    FORBIDDEN("COMMON-SYSTEM-005", "Forbidden"),
    VALIDATION_ERROR("COMMON-SYSTEM-006", "Validation error"),
    CONFLICT("COMMON-SYSTEM-007", "Conflict"),
    OPTIMISTIC_LOCK_ERROR("COMMON-SYSTEM-010", "Optimistic lock error");
}

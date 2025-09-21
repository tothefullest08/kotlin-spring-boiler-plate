package harry.boilerplate.common.exception

abstract class ApplicationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    abstract val errorCode: ErrorCode
}

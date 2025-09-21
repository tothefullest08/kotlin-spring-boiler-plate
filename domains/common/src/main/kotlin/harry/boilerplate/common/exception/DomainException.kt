package harry.boilerplate.common.exception

abstract class DomainException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    abstract val errorCode: ErrorCode
}

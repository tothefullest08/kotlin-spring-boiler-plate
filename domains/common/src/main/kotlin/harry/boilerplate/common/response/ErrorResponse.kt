package harry.boilerplate.common.response

data class ErrorResponse(
    val code: String,
    val message: String,
    val path: String?
) {
    companion object {
        fun of(code: String, message: String, path: String?): ErrorResponse = ErrorResponse(code, message, path)
    }
}

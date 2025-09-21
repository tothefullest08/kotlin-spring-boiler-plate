package harry.boilerplate.common.exception

import harry.boilerplate.common.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Domain exception occurred: {}", ex.message, ex)
        val errorResponse = ErrorResponse.of(ex.errorCode.code, ex.errorCode.message, request.requestURI)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Application exception occurred: {}", ex.message, ex)
        val errorResponse = ErrorResponse.of(ex.errorCode.code, ex.errorCode.message, request.requestURI)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("Validation exception occurred: {}", ex.message)
        val message = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Validation failed"
        val errorResponse = ErrorResponse.of(CommonSystemErrorCode.VALIDATION_ERROR.code, message, request.requestURI)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Bind exception occurred: {}", ex.message)
        val message = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Validation failed"
        val errorResponse = ErrorResponse.of(CommonSystemErrorCode.VALIDATION_ERROR.code, message, request.requestURI)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("IllegalArgumentException occurred: {}", ex.message, ex)
        val errorResponse = ErrorResponse.of(CommonSystemErrorCode.INVALID_REQUEST.code, ex.message ?: "Invalid request", request.requestURI)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("Unsupported media type: {}", ex.message)
        val message = "Content-Type '${ex.contentType}' is not supported"
        val errorResponse = ErrorResponse.of(CommonSystemErrorCode.INVALID_REQUEST.code, message, request.requestURI)
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("HttpMessageNotReadableException occurred: {}", ex.message)
        val errorResponse = ErrorResponse.of(CommonSystemErrorCode.INVALID_REQUEST.code, "Malformed JSON request", request.requestURI)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unexpected exception occurred: {}", ex.message, ex)
        val errorResponse = ErrorResponse.of(
            CommonSystemErrorCode.INTERNAL_SERVER_ERROR.code,
            CommonSystemErrorCode.INTERNAL_SERVER_ERROR.message,
            request.requestURI
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}

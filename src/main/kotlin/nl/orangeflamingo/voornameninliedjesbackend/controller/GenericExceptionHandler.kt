package nl.orangeflamingo.voornameninliedjesbackend.controller

import jakarta.servlet.http.HttpServletRequest
import nl.orangeflamingo.voornameninliedjesbackend.model.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.OffsetDateTime

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class GenericExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDenied(ex: AuthorizationDeniedException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            OffsetDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.reasonPhrase,
            ex.message ?: "Access denied"
        )

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception at ${request.requestURI}", ex)

        val response = ErrorResponse(
            OffsetDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            "An unexpected error occurred"
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(ex: NoResourceFoundException, request: HttpServletRequest): ResponseEntity<Unit> {
        val uri = request.requestURI
        val ip =
            if (request.getHeader("X-Forwarded-For") != null) request.getHeader("X-Forwarded-For") else request.remoteAddr
        logger.warn("Static resource not found: uri={} ip={}", uri, ip)

        return ResponseEntity.notFound().build()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GenericExceptionHandler::class.java)
    }
}

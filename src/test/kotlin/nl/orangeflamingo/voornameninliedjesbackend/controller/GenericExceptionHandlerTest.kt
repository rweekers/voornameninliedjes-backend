package nl.orangeflamingo.voornameninliedjesbackend.controller

import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.servlet.resource.NoResourceFoundException

class GenericExceptionHandlerTest {

    private val handler = GenericExceptionHandler()

    @Test
    fun `handleAuthorizationDenied returns 403`() {
        val ex = AuthorizationDeniedException("Denied")
        val response = handler.handleAuthorizationDenied(ex)

        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        assertNotNull(response.body)
        assertEquals(HttpStatus.FORBIDDEN.value(), response.body?.status)
        assertEquals("Denied", response.body?.message)
    }

    @Test
    fun `handleUnexpected returns 500`() {
        val request = object : HttpServletRequest by mockk() {
            override fun getRequestURI(): String = "/goes-wrong"
        }
        val ex = RuntimeException("Something went wrong")

        val response = handler.handleUnexpected(ex, request)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.body?.status)
        assertEquals("An unexpected error occurred", response.body?.message)
    }

    @Test
    fun `handleNoResourceFound returns 404`() {
        val request = object : HttpServletRequest by mockk() {
            override fun getRequestURI(): String = "/static/file.txt"
            override fun getHeader(name: String?): String? = null
            override fun getRemoteAddr(): String = "127.0.0.1"
        }
        val ex = NoResourceFoundException(HttpMethod.GET, "/static/", "file.txt")

        val response = handler.handleNoResourceFound(ex, request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }
}

package nl.orangeflamingo.voornameninliedjesbackend.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class RequestLoggingInterceptorTest {

    private val interceptor = RequestLoggingInterceptor()

    private val request = mock(HttpServletRequest::class.java)

    private val response = mock(HttpServletResponse::class.java)

    private val handler = mock(Any::class.java)

    private val authentication = mock(Authentication::class.java)

    @BeforeEach
    fun setUp() {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
    }

    @Test
    fun `should log authenticated user and request info`() {
        val logger: Logger = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java) as Logger
        val appender: ListAppender<ILoggingEvent> = ListAppender()
        appender.start()
        logger.addAppender(appender)

        whenever(authentication.isAuthenticated).thenReturn(true)
        whenever(authentication.name).thenReturn("test-user")

        whenever(request.method).thenReturn("GET")
        whenever(request.requestURI).thenReturn("/api/test")
        whenever(request.remoteAddr).thenReturn("127.0.0.1")
        whenever(request.getHeader("X-User-Agent")).thenReturn("Mozilla/5.0 (X11; Linux x86_64; rv:127.0) Gecko/20100101 Firefox/127.0")
        whenever(request.getHeader("X-Forwarded-For")).thenReturn(null)
        whenever(request.getHeader("X-Referer")).thenReturn("https://example.com")
        whenever(request.getHeader("X-Accept-Language")).thenReturn("en-US")

        val result = interceptor.preHandle(request, response, handler)

        assertTrue(result)
        val logs = appender.list
        assertTrue(logs.filter { it.level == Level.INFO }.size == 3)
        assertTrue(logs.any { it.formattedMessage.contains("Authenticated user: test-user") })
        assertTrue(logs.any { it.formattedMessage.contains("Request: [GET] /api/test from IP=127.0.0.1") })
        assertTrue(logs.any { it.formattedMessage.contains("Browser Firefox:127.0.null on Linux:null.null.null.null and device Other") })
        appender.stop()
    }
}

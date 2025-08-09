package nl.orangeflamingo.voornameninliedjesbackend.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class RequestLoggingInterceptorTest {

    private val interceptor = RequestLoggingInterceptor()

    private val request: HttpServletRequest = mockk()

    private val response: HttpServletResponse = mockk()

    private val handler: Any = mockk()

    private val authentication: Authentication = mockk()

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

        every { authentication.isAuthenticated } returns true
        every { authentication.name } returns "test-user"

        every { request.method } returns "GET"
        every { request.requestURI } returns "/api/test"
        every { request.remoteAddr } returns "127.0.0.1"
        every { request.getHeader("X-User-Agent") } returns "Mozilla/5.0 (X11; Linux x86_64; rv:127.0) Gecko/20100101 Firefox/127.0"
        every { request.getHeader("X-Forwarded-For") } returns null
        every { request.getHeader("X-Referer") } returns "https://example.com"
        every { request.getHeader("X-Accept-Language") } returns "en-US"

        val result = interceptor.preHandle(request, response, handler)

        assertThat(result).isTrue()
        val logs = appender.list
        assertThat(logs.filter { it.level == Level.INFO }.size == 3).isTrue()
        assertThat(logs.any { it.formattedMessage.contains("Authenticated user: test-user") }).isTrue()
        assertThat(logs.any { it.formattedMessage.contains("Request: [GET] /api/test from IP=127.0.0.1") }).isTrue()
        assertThat(logs.any { it.formattedMessage.contains("Browser Firefox:127.0 on operating system Linux and device Other") }).isTrue()
        appender.stop()
    }
}

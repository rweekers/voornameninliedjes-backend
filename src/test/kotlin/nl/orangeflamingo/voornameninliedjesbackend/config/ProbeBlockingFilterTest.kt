package nl.orangeflamingo.voornameninliedjesbackend.config

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProbeBlockingFilterTest {

    private lateinit var filter: ProbeBlockingFilter
    private val chain: FilterChain = mockk()
    private val request: HttpServletRequest = mockk()
    private val response: HttpServletResponse = mockk()

    @BeforeEach
    fun setup() {
        filter = ProbeBlockingFilter()

        every { request.getHeader("X-Forwarded-For") } returns ""
        every { chain.doFilter(any(), any()) } just runs
    }

    @Test
    fun `normal request should pass through filter`() {
        every { request.requestURI } returns "/api/normal"
        every { request.remoteAddr } returns "1.2.3.4"

        filter.doFilterInternal(request, response, chain)

        verify { chain.doFilter(request, response) }
        verify(exactly = 0) { response.status = 404 }
    }

    @Test
    fun `probe request should be blocked`() {
        every { request.requestURI } returns "/admin.php"
        every { request.remoteAddr } returns "5.6.7.8"
        every { response.status = any() } just runs

        filter.doFilterInternal(request, response, chain)

        verify(exactly = 0) { chain.doFilter(request, response) }
        verify { response.status = HttpServletResponse.SC_NOT_FOUND }
    }

    @Test
    fun `subsequent request from blacklisted IP should be blocked`() {
        val ip = "9.8.7.6"

        // First request triggers blacklisting
        every { request.requestURI } returns "/wp-login.php"
        every { request.remoteAddr } returns ip
        every { response.status = any() } just runs
        filter.doFilterInternal(request, response, chain)

        // Reset mocks for second request
        clearMocks(chain, response)
        every { request.requestURI } returns "/api/normal"
        every { request.remoteAddr } returns ip
        every { response.status = any() } just runs

        filter.doFilterInternal(request, response, chain)

        verify(exactly = 0) { chain.doFilter(request, response) }
        verify { response.status = HttpServletResponse.SC_NOT_FOUND }
    }

    @Test
    fun `X-Forwarded-For header should be used for IP`() {
        val forwardedIp = "11.22.33.44"
        every { request.requestURI } returns "/admin.php"
        every { request.getHeader("X-Forwarded-For") } returns "$forwardedIp, 1.2.3.4"
        every { request.remoteAddr } returns "1.2.3.4"
        every { response.status = any() } just runs

        filter.doFilterInternal(request, response, chain)

        verify(exactly = 0) { chain.doFilter(request, response) }
        verify { response.status = HttpServletResponse.SC_NOT_FOUND }
    }
}

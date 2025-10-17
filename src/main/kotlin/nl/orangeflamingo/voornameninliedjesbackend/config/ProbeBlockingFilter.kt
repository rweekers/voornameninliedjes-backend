package nl.orangeflamingo.voornameninliedjesbackend.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

@Component
class ProbeBlockingFilter : OncePerRequestFilter() {

    private val probePattern: Pattern = Pattern.compile(
        "(?i).*(?:\\.php\$|\\.phtml\$|wp-login\\.php\$|admin\\.php\$|xmlrpc\\.php\$|\\.env\$)"
    )

    private val blacklist = ConcurrentHashMap<String, Long>()
    private val blacklistSeconds: Long = 60 * 60

    @Throws(ServletException::class, IOException::class)
    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI
        val remoteIp = extractClientIp(request)

        // check if IP is blacklisted
        val until = blacklist[remoteIp]
        if (until != null && Instant.now().epochSecond < until) {
            response.status = HttpServletResponse.SC_NOT_FOUND
            return
        }

        if (probePattern.matcher(uri).matches()) {
            log.warn("Probe detected: uri=$uri ip=$remoteIp")

            blacklist[remoteIp] = Instant.now().epochSecond + blacklistSeconds

            response.status = HttpServletResponse.SC_NOT_FOUND
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun extractClientIp(request: HttpServletRequest): String {
        val xff = request.getHeader("X-Forwarded-For")
        return if (!xff.isNullOrBlank()) {
            xff.split(",")[0].trim()
        } else {
            request.remoteAddr
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger("fail2ban")
    }
}

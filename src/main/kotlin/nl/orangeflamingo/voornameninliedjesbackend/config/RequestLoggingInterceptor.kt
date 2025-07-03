package nl.orangeflamingo.voornameninliedjesbackend.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import ua_parser.Client
import ua_parser.Parser


@Component
class RequestLoggingInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        if (auth.isAuthenticated) {
            log.info("Authenticated user: {}", auth.name)
        }

        val xForwardedFor = request.getHeader("X-Forwarded-For")
        val remoteAddr = request.remoteAddr
        val clientIp = if (xForwardedFor != null) xForwardedFor.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].trim { it <= ' ' } else remoteAddr

        val method = request.method
        val uri = request.requestURI
        val userAgent = request.getHeader("User-Agent")
        val uaParser = Parser()
        val c: Client = uaParser.parse(userAgent)

        val browser = c.userAgent.family
        val browserMajorVersion = c.userAgent.major
        val browserMinorVersion = c.userAgent.minor
        val browserPatchVersion = c.userAgent.patch

        val operatingSystem = c.os.family
        val operatingSystemMajorVersion = c.os.major
        val operatingSystemSystemMinorVersion = c.os.minor
        val operatingSystemPatchVersion = c.os.patch
        val operatingSystemPatchMinorVersion = c.os.patchMinor

        val device = c.device.family

        val referer = request.getHeader("Referer")

        log.info("Request: [{}] {} from IP={}, UA={}, Referer={}", method, uri, clientIp, userAgent, referer)
        log.info("Browser $browser:$browserMajorVersion.$browserMinorVersion.$browserPatchVersion on $operatingSystem:$operatingSystemMajorVersion.$operatingSystemSystemMinorVersion.$operatingSystemPatchVersion.$operatingSystemPatchMinorVersion and device $device")
        return true
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)
    }
}
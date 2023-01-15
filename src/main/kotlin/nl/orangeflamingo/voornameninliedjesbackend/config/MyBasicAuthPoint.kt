package nl.orangeflamingo.voornameninliedjesbackend.config

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.springframework.stereotype.Component
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class MyBasicAuthPoint: BasicAuthenticationEntryPoint() {

    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        response.addHeader("WWW-Authenticate", "Basic realm=$realmName")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.println("HTTP Status 401 - ${authException.message}")
    }

    @Override
    override fun afterPropertiesSet() {
        realmName = "OrangeFlamingo"
        super.afterPropertiesSet()
    }
}
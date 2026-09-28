package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.CurrentUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class CurrentUserServiceImpl : CurrentUserService {
    override fun currentUser(): CurrentUser {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authenticated user")

        check(authentication.isAuthenticated) { "No authenticated user" }

        val jwt = authentication.principal as? Jwt
            ?: throw IllegalStateException("No JWT principal available")

        return CurrentUser(
            id = jwt.subject ?: throw IllegalStateException("JWT has no subject"),
            username = jwt.getClaimAsString("preferred_username")
                ?: throw IllegalStateException("JWT has no preferred_username")
        )
    }
}
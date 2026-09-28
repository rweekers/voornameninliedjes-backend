package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class CurrentUserServiceTest {

    private val service = CurrentUserServiceImpl()

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should return current user from JWT`() {
        val jwt = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .subject("123")
            .claim("preferred_username", "remco")
            .build()

        SecurityContextHolder.getContext().authentication =
            JwtAuthenticationToken(jwt, emptyList())

        val result = service.currentUser()

        assertThat(result.id).isEqualTo("123")
        assertThat(result.username).isEqualTo("remco")
    }

    @Test
    fun `should throw when there is no authentication`() {
        assertThrows<IllegalStateException> {
            service.currentUser()
        }
    }

    @Test
    fun `should throw when authentication is not authenticated`() {
        val authentication = mockk<UsernamePasswordAuthenticationToken> {
            every { isAuthenticated } returns false
        }

        SecurityContextHolder.getContext().authentication = authentication

        assertThrows<IllegalStateException> {
            service.currentUser()
        }
    }

    @Test
    fun `should throw when principal is not a JWT`() {
        val authentication = mockk<UsernamePasswordAuthenticationToken> {
            every { isAuthenticated } returns true
            every { principal } returns "not-a-jwt"
        }

        SecurityContextHolder.getContext().authentication = authentication

        assertThrows<IllegalStateException> {
            service.currentUser()
        }
    }
}
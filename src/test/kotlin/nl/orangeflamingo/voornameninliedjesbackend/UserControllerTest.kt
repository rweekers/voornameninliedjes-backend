package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.controller.UserDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.domain.UserRole
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.util.Base64Utils

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UserControllerTest(
    @Autowired val client: WebTestClient,
    @Autowired val userRepository: UserRepository,
    @Autowired val encoder: PasswordEncoder
) {

    private val adminUser: String = "admin"
    private val adminPassword: String = "secret"
    private val adminRole: String = "ADMIN"
    private val ownerUser: String = "owner"
    private val ownerPassword: String = "verysecret"
    private val ownerRole: String = "OWNER"

    @BeforeEach
    fun createUser() {
        userRepository.deleteAll()
        userRepository.saveAll(
            listOf(
                User(
                    username = adminUser,
                    password = encoder.encode(adminPassword),
                    roles = mutableSetOf(UserRole(adminRole))
                ),
                User(
                    username = ownerUser,
                    password = encoder.encode(ownerPassword),
                    roles = mutableSetOf(UserRole(ownerRole))
                )
            )
        )
    }

    @Test
    fun userControllerUnauthorizedTest() {
        client.get()
            .uri("/gamma/users")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun userControllerIncorrectRoleTest() {
        client.get()
            .uri("/gamma/users")
            .header(
                "Authorization",
                "Basic ${Base64Utils.encodeToString("$adminUser:$adminPassword".toByteArray(Charsets.UTF_8))}"
            )
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun userControllerTest() {
        client.get()
            .uri("/gamma/users")
            .header(
                "Authorization",
                "Basic ${Base64Utils.encodeToString("$ownerUser:$ownerPassword".toByteArray(Charsets.UTF_8))}"
            )
            .exchange()
            .expectStatus().isOk
            .expectBodyList<UserDto>().hasSize(2)
    }
}


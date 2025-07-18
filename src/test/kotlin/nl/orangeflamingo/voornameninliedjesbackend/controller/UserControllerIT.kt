package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.domain.UserRole
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
import kotlin.random.Random

class UserControllerIT : AbstractIntegrationTest() {

    @Autowired
    private lateinit var client: WebTestClient
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var encoder: PasswordEncoder

    private val adminUser: String = "admin"
    private val adminPassword: String = "secret"
    private val adminRole: String = "ADMIN"
    private val ownerUser: String = "owner"
    private val ownerPassword: String = "verysecret"
    private val ownerRole: String = "OWNER"
    private lateinit var userMap: Map<String, Long>

    @BeforeEach
    fun createUser() {
        userRepository.deleteAll()
        userMap = userRepository.saveAll(
            listOf(
                User(
                    username = adminUser,
                    password = encoder.encode(adminPassword),
                    roles = mutableSetOf(UserRole(1, adminRole))
                ),
                User(
                    username = ownerUser,
                    password = encoder.encode(ownerPassword),
                    roles = mutableSetOf(UserRole(2, ownerRole))
                )
            )
        ).associate { it.username to it.id!! }
    }

    @Test
    fun authenticateWrongPasswordTest() {
        client.post()
            .uri("/admin/authenticate")
            .body(
                BodyInserters.fromValue(
                    UserDto(
                        username = adminUser,
                        password = "wrongPassword"
                    )
                )
            )
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun authenticateWrongUserTest() {
        client.post()
            .uri("/admin/authenticate")
            .body(
                BodyInserters.fromValue(
                    UserDto(
                        username = "nonExistingUser",
                        password = "nonExistingPassword"
                    )
                )
            )
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun authenticateCorrectTest() {
        client.post()
            .uri("/admin/authenticate")
            .body(
                BodyInserters.fromValue(
                    UserDto(
                        username = adminUser,
                        password = adminPassword
                    )
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.username").isNotEmpty
            .jsonPath("$.username").isEqualTo(adminUser)
    }

    @Test
    fun newUserTest() {
        client.post()
            .uri("/admin/users")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(ownerUser, ownerPassword) }
            .body(
                BodyInserters.fromValue(
                    UserDto(
                        username = "newUser",
                        password = "newUserPassword",
                        roles = mutableSetOf("ADMIN")
                    )
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.username").isNotEmpty
            .jsonPath("$.username").isEqualTo("newUser")
    }

    @Test
    fun deleteUserTest() {
        client.delete()
            .uri("/admin/users/${userMap[adminUser]}")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(ownerUser, ownerPassword) }
            .exchange()
            .expectStatus().isOk
        assertNull(userRepository.findByUsername(adminUser))
    }

    @Test
    fun userWrongCredentialsTest() {
        client.get()
            .uri("/admin/users")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(adminUser, "wrongPassword") }
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun userUnauthorizedTest() {
        client.get()
            .uri("/admin/users")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun userAuthorizationRoleTest() {
        client.get()
            .uri("/admin/users")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(adminUser, adminPassword) }
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun getUsersTest() {
        client.get()
            .uri("/admin/users")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(ownerUser, ownerPassword) }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<UserDto>().hasSize(2)
    }

    @Test
    fun getAdminUserTest() {
        client.get()
            .uri("/admin/users/${userMap[adminUser]}")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(ownerUser, ownerPassword) }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.username").isNotEmpty
            .jsonPath("$.username").isEqualTo(adminUser)
    }

    @Test
    fun userNotFoundUserTest() {
        val maxId = userMap.values.maxOrNull()!!
        val notExistingId = Random.nextLong(maxId, Long.MAX_VALUE)

        client.get()
            .uri("/admin/users/$notExistingId")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(ownerUser, ownerPassword) }
            .exchange()
            .expectStatus().isBadRequest
    }
}


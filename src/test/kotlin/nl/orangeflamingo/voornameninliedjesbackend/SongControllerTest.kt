package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.domain.DbSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoUserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.util.Base64Utils
import kotlin.text.Charsets.UTF_8

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SongControllerTest(
    @Autowired val client: WebTestClient,
    @Autowired val userRepository: MongoUserRepository,
    @Autowired val encoder: PasswordEncoder
) {

    private val user: String = "test"
    private val password: String = "secret"
    private val role: String = "ADMIN"

    @BeforeEach
    fun createUser() {
        userRepository.save(
            User(
                username = user,
                password = encoder.encode(password),
                roles = mutableSetOf(role)
            )
        )
    }

    @Test
    fun songControllerTest() {
        client.get()
            .uri("/api/songs")
            .header(
                "Authorization", "Basic ${Base64Utils.encodeToString("$user:$password".toByteArray(UTF_8))}"
            )
            .exchange()
            .expectStatus().isOk
            .expectBodyList<DbSong>().hasSize(0)
    }

}


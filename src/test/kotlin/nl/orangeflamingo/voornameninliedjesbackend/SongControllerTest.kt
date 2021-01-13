package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.domain.DbSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.domain.UserRole
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoSongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
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
import kotlin.text.Charsets.UTF_8

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SongControllerTest(
    @Autowired val client: WebTestClient,
    @Autowired val userRepository: UserRepository,
    @Autowired val mongoSongRepository: MongoSongRepository,
    @Autowired val songRepository: SongRepository,
    @Autowired val encoder: PasswordEncoder
) {

    private val user: String = "test"
    private val password: String = "secret"
    private val adminRole: String = "ADMIN"

    @BeforeEach
    fun createUser() {
        mongoSongRepository.deleteAll()
        songRepository.deleteAll()
        userRepository.deleteAll()
        userRepository.save(
            User(
                username = user,
                password = encoder.encode(password),
                roles = mutableSetOf(UserRole(adminRole))
            )
        )
    }

    @Test
    fun songControllerTest() {
        client.get()
            .uri("/api/songs")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<DbSong>().hasSize(0)
    }

    @Test
    fun songControllerBetaTest() {
        client.get()
            .uri("/beta/songs")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<DbSong>().hasSize(0)
    }

    @Test
    fun songAdminControllerBetaTest() {
        client.get()
            .uri("/gamma/songs")
            .header(
                "Authorization", "Basic ${Base64Utils.encodeToString("$user:$password".toByteArray(UTF_8))}"
            )
            .exchange()
            .expectStatus().isOk
            .expectBodyList<DbSong>().hasSize(0)
    }

    @Test
    fun songAdminControllerBetaUnauthorizedTest() {
        client.get()
            .uri("/gamma/songs")
            .exchange()
            .expectStatus().isUnauthorized
    }

}


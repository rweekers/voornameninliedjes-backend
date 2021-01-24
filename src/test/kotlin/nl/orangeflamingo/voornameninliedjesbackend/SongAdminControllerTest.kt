package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSongDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
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
class SongAdminControllerTest(
    @Autowired val client: WebTestClient,
    @Autowired val userRepository: UserRepository,
    @Autowired val songRepository: SongRepository,
    @Autowired val artistRepository: ArtistRepository,
    @Autowired val encoder: PasswordEncoder
) {

    private val user: String = "test"
    private val password: String = "secret"
    private val adminRole: String = "ADMIN"

    @BeforeEach
    fun createUser() {
        songRepository.deleteAll()
        artistRepository.deleteAll()
        userRepository.deleteAll()
        userRepository.save(
            User(
                username = user,
                password = encoder.encode(password),
                roles = mutableSetOf(UserRole(adminRole))
            )
        )
        val artist = artistRepository.save(
            Artist(
                name = "The Beatles"
            )
        )

        songRepository.saveAll(
            listOf(
                Song(
                    title = "Michelle",
                    name = "Michelle",
                    status = SongStatus.SHOW,
                    artists = mutableSetOf(
                        ArtistRef(
                            artist = artist.id!!
                        )
                    )
                ),
                Song(
                    title = "Lucy in the Sky with Diamonds",
                    name = "Lucy",
                    status = SongStatus.SHOW,
                    artists = mutableSetOf(
                        ArtistRef(
                            artist = artist.id!!
                        )
                    )
                )
            )
        )
    }

    @Test
    fun getAllSongsTest() {
        client.get()
            .uri("/admin/songs")
            .header(
                "Authorization", "Basic ${Base64Utils.encodeToString("$user:$password".toByteArray(UTF_8))}"
            )
            .exchange()
            .expectStatus().isOk
            .expectBodyList<AdminSongDto>().hasSize(2)
    }

    @Test
    fun getSongsByNameTest() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/admin/songs")
                    .queryParam("name", "Lucy")
                    .build()
            }
            .header(
                "Authorization", "Basic ${Base64Utils.encodeToString("$user:$password".toByteArray(UTF_8))}"
            )
            .exchange()
            .expectStatus().isOk
            .expectBodyList<AdminSongDto>().hasSize(1)
    }

    @Test
    fun getSongsUnauthorizedTest() {
        client.get()
            .uri("/admin/songs")
            .exchange()
            .expectStatus().isUnauthorized
    }

}


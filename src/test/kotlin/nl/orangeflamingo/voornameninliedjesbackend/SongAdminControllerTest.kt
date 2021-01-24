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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
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
    private lateinit var songMap: Map<String, Long>

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

        val songMichelle = Song(
            title = "Michelle",
            name = "Michelle",
            status = SongStatus.SHOW,
            artists = mutableSetOf(
                ArtistRef(
                    artist = artist.id!!
                )
            )
        )

        songMap = songRepository.saveAll(
            listOf(
                songMichelle, songMichelle.copy(title = "Lucy in the Sky with Diamonds", name = "Lucy")
            )
        ).map { it.title to it.id!! }.toMap()
    }

    @Test
    fun getAllSongsTest() {
        client.get()
            .uri("/admin/songs")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
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
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<AdminSongDto>().hasSize(1)
    }

    @Test
    fun getSongsByWithNameStartingWithTest() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/admin/songs")
                    .queryParam("first-character", "M")
                    .build()
            }
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<AdminSongDto>().hasSize(1)
    }

    @Test
    fun getSongByIdTest() {
        client.get()
            .uri("/admin/songs/${songMap["Michelle"]}")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isNotEmpty
            .jsonPath("$.title").isEqualTo("Michelle")
    }

    @Test
    fun getSongsUnauthorizedTest() {
        client.get()
            .uri("/admin/songs")
            .exchange()
            .expectStatus().isUnauthorized
    }

}


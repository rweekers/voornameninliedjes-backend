package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSongDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters

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
    private val artistName: String = "The Beatles"
    private val songTitle: String = "Michelle"
    private val artistMap: MutableMap<String, Long> = mutableMapOf()
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
                name = artistName
            )
        )
        artistMap[artistName] = artist.id!!

        val songMichelle = TestSong(
            title = songTitle,
            name = "Michelle",
            artists = mutableSetOf(
                ArtistRef(
                    artist = artist.id!!
                )
            )
        ).toDomain()

        val songMadonna = TestSong(
            title = "Lady Madonna",
            name = "Madonna",
            artists = mutableSetOf(
                ArtistRef(
                    artist = artist.id!!
                )
            ),
            status = SongStatus.IN_PROGRESS
        ).toDomain()

        val songLucy = TestSong(
            artists = mutableSetOf(
                ArtistRef(
                    artist = artist.id!!
                )
            )
        ).toDomain()

        songMap = songRepository.saveAll(
            listOf(
                songMichelle, songMadonna, songLucy
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
            .expectBodyList<AdminSongDto>().hasSize(3)
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
                    .queryParam("status", "SHOW,IN_PROGRESS,TO_BE_DELETED")
                    .build()
            }
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<AdminSongDto>().hasSize(2)
    }

    @Test
    fun getSongsByWithNameStartingWithTestAndStatusIn() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/admin/songs")
                    .queryParam("first-character", "M")
                    .queryParam("status", "IN_PROGRESS")
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
            .uri("/admin/songs/${songMap[songTitle]}")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isNotEmpty
            .jsonPath("$.title").isEqualTo(songTitle)
    }

    @Test
    fun newSongTest() {
        client.post()
            .uri("/admin/songs/temp")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .body(
                BodyInserters.fromValue(
                    AdminSongDto(
                        artist = "newArtist",
                        title = "newTitle with newName",
                        name = "newName",
                        status = "IN_PROGRESS"
                    )
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isNotEmpty
            .jsonPath("$.title").isEqualTo("newTitle with newName")
            .jsonPath("$.artist").isNotEmpty
            .jsonPath("$.artist").isEqualTo("newArtist")
    }

    @Test
    fun replaceSongTest() {
        val id = songMap[songTitle]!!

        client.put()
            .uri("/admin/songs/temp/$id")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .body(
                BodyInserters.fromValue(
                    AdminSongDto(
                        id = id.toString(),
                        artist = "newArtist",
                        title = "newTitle with newName",
                        name = "newName",
                        status = "IN_PROGRESS"
                    )
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isNotEmpty
            .jsonPath("$.title").isEqualTo("newTitle with newName")
            .jsonPath("$.artist").isNotEmpty
            .jsonPath("$.artist").isEqualTo("newArtist")
    }

    @Test
    fun addFlickrPhotoForSong() {
        val id = songMap[songTitle]!!
        val newFlickrId = "15"

        client.post()
            .uri("/admin/songs/temp/$id/$newFlickrId")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
        assertEquals(1, artistRepository.findById(artistMap[artistName]!!).orElseThrow().flickrPhotos.size)
    }

    @Test
    fun addFlickrPhotoForArtist() {
        val id = artistMap[artistName]!!
        val newFlickrId = "15"

        client.post()
            .uri("/admin/artists/temp/$id/$newFlickrId")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
        assertEquals(1, artistRepository.findById(id).orElseThrow().flickrPhotos.size)
    }

    @Test
    fun getSongsUnauthorizedTest() {
        client.get()
            .uri("/admin/songs")
            .exchange()
            .expectStatus().isUnauthorized
    }

}


package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminWikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters

class SongAdminControllerIT : AbstractIntegrationTest() {

    @Autowired
    private lateinit var client: WebTestClient
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var songRepository: SongRepository
    @Autowired
    private lateinit var artistRepository: ArtistRepository
    @Autowired
    private lateinit var encoder: PasswordEncoder

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
                roles = mutableSetOf(UserRole(1, adminRole))
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
            wikimediaPhotos = mutableSetOf(
                SongPhoto(
                    url = "https://somefakewikimediaphotourl.doesnotexist",
                    attribution = "Attribution for test wikimedia photo"
                )
            ),
            artist = artist.id!!
        ).toDomain()

        val songMadonna = TestSong(
            title = "Lady Madonna",
            name = "Madonna",
            artist = artist.id!!,
            status = SongStatus.INCOMPLETE
        ).toDomain()

        val songLucy = TestSong(
            artist = artist.id!!,
            wikimediaPhotos = mutableSetOf(SongPhoto(url = "some url", attribution = "some attribution"))
        ).toDomain()

        songMap = songRepository.saveAll(
            listOf(
                songMichelle, songMadonna, songLucy
            )
        ).associate { it.title to it.id!! }
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
                    .queryParam("status", "SHOW,IN_PROGRESS,INCOMPLETE, TO_BE_DELETED")
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
                    .queryParam("status", "INCOMPLETE")
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
    fun getSongNofFoundByIdTest() {
        client.get()
            .uri("/admin/songs/${songMap.values.maxOf { it } + 1}")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
            .expectStatus().isNotFound
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
                        songWikimediaPhotos = listOf(
                            AdminWikimediaPhotoDto(
                                url = "https://testurl.doesnotexist",
                                attribution = "some test attribution"
                            )
                        ),
                        status = "IN_PROGRESS",
                        hasDetails = true
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
            .jsonPath("$.songWikimediaPhotos").isNotEmpty
            .jsonPath("$.songWikimediaPhotos[0].url").isEqualTo("https://testurl.doesnotexist")
            .jsonPath("$.songWikimediaPhotos[0].attribution").isEqualTo("some test attribution")
            .jsonPath("$.hasDetails").isNotEmpty
            .jsonPath("$.hasDetails").isEqualTo("true")
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
                        songWikimediaPhotos = listOf(
                            AdminWikimediaPhotoDto(
                                url = "https://testurl.doesnotexist",
                                attribution = "some test attribution"
                            )
                        ),
                        status = "INCOMPLETE",
                        remarks = "Geen spotify link gevonden...",
                        hasDetails = true
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
            .jsonPath("$.songWikimediaPhotos").isNotEmpty
            .jsonPath("$.songWikimediaPhotos[0].url").isEqualTo("https://testurl.doesnotexist")
            .jsonPath("$.songWikimediaPhotos[0].attribution").isEqualTo("some test attribution")
            .jsonPath("$.hasDetails").isNotEmpty
            .jsonPath("$.hasDetails").isEqualTo("true")
            .jsonPath("$.status").isNotEmpty
            .jsonPath("$.status").isEqualTo("INCOMPLETE")
            .jsonPath("$.remarks").isNotEmpty
            .jsonPath("$.remarks").isEqualTo("Geen spotify link gevonden...")
    }

    @Test
    fun enrichLastFmInfoForSongs() {
        client.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/admin/songs/enrich-lastfm")
                    .queryParam("update-all", "true")
                    .build()
            }
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun enrichWikipediaForSongs() {
        client.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/admin/songs/enrich-wikipedia")
                    .queryParam("update-all", "true")
                    .build()
            }
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(user, password) }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun getSongsUnauthorizedTest() {
        client.get()
            .uri("/admin/songs")
            .exchange()
            .expectStatus().isUnauthorized
    }

}


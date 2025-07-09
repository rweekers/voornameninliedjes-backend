package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.domain.UserRole
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.model.ArtistInputDto
import nl.orangeflamingo.voornameninliedjesbackend.model.PhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
import org.testcontainers.shaded.com.google.common.net.HttpHeaders
import java.net.URI

class ArtistControllerIT : AbstractIntegrationTest() {

    private val nonExistingArtistId = 100

    private lateinit var artistMap: Map<String, Long>
    @Autowired
    private lateinit var client: WebTestClient
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var encoder: PasswordEncoder
    @Autowired
    private lateinit var songRepository: SongRepository
    @MockitoSpyBean
    private lateinit var artistRepository: ArtistRepository

    private val adminUser: String = "admin"
    private val adminPassword: String = "secret"
    private val adminRole: String = "ADMIN"

    @BeforeEach
    fun createUser() {
        userRepository.deleteAll()
        userRepository.saveAll(
            listOf(
                User(
                    username = adminUser,
                    password = encoder.encode(adminPassword),
                    roles = mutableSetOf(UserRole(1, adminRole))
                )
            )
        )
        songRepository.deleteAll()
        artistRepository.deleteAll()
        val artist = Artist(
            name = "The Beatles",
            photos = mutableSetOf(
                ArtistPhoto(
                    url = URI.create("https://upload.wikimedia.org/wikipedia/commons/6/61/The_Beatles_arrive_at_JFK_Airport.jpg"),
                    attribution = "United Press International, Public domain, via Wikimedia Commons"
                )
            )
        )
        artistMap = artistRepository.saveAll(
            listOf(
                artist,
                artist.copy(
                    name = "The Rolling Stones",
                    photos = mutableSetOf()
                )
            )
        ).associate { it.name to it.id!! }
    }

    @Test
    fun getAllArtistsTest() {
        client.get()
            .uri("/api/artists")
            .header("Accept", "application/vnd.voornameninliedjes.artists.v1+json")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<ArtistDto>().hasSize(2)
    }

    @Test
    fun getAllArtistsV2() {
        client.get()
            .uri("/api/artists")
            .header(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<ArtistDto>().hasSize(2)
    }

    @Test
    fun getArtistByIdTest() {
        client.get()
            .uri("/api/artists/${artistMap["The Beatles"]}")
            .header("Accept", "application/vnd.voornameninliedjes.artists.v1+json")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isNotEmpty
            .jsonPath("$.name").isEqualTo("The Beatles")
    }

    @Test
    fun getArtistByIdTestV2() {
        client.get()
            .uri("/api/artists/${artistMap["The Beatles"]}")
            .header(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isNotEmpty
            .jsonPath("$.name").isEqualTo("The Beatles")
    }

    @Test
    fun getArtistByIdNotFoundTestV2() {
        client.get()
            .uri("/api/artists/$nonExistingArtistId")
            .header(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.error").isNotEmpty
            .jsonPath("$.error").isEqualTo("Not Found")
            .jsonPath("$.message").isNotEmpty
            .jsonPath("$.message").isEqualTo("Artist with id $nonExistingArtistId not found")
    }

    @Test
    fun deleteArtistByIdTestV2() {
        client.delete()
            .uri("/api/artists/${artistMap["The Beatles"]}")
            .headers { headers ->
                headers.set(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
                headers.setBasicAuth(adminUser, adminPassword)
            }
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun createArtistTestV2() {
        client.post()
            .uri("/api/artists")
            .headers { headers ->
                headers.set(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
                headers.setBasicAuth(adminUser, adminPassword)
            }
            .body(
                BodyInserters.fromValue(
                    ArtistInputDto(
                        "newArtist",
                        listOf(PhotoDto(URI.create("https://image.nl/artist1"), "Some attribution"))
                    )
                )
            )
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun createArtistWithExistingNameTestV2() {
        client.post()
            .uri("/api/artists")
            .headers { headers ->
                headers.set(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
                headers.setBasicAuth(adminUser, adminPassword)
            }
            .body(
                BodyInserters.fromValue(
                    ArtistInputDto("The Beatles", emptyList())
                )
            )
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun updateArtistTestV2() {
        client.put()
            .uri("/api/artists/${artistMap["The Beatles"]}")
            .headers { headers ->
                headers.set(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
                headers.setBasicAuth(adminUser, adminPassword)
            }
            .body(
                BodyInserters.fromValue(
                    ArtistInputDto("Updated name", emptyList())
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isNotEmpty
            .jsonPath("$.name").isEqualTo("Updated name")
    }

    @Test
    fun getArtistByIdUnknownExceptionTestV2() {
        whenever(
            artistRepository.findById(
                artistMap["The Beatles"] ?: throw IllegalStateException()
            )
        ).doAnswer { throw RuntimeException("Unknown exception") }

        client.get()
            .uri("/api/artists/${artistMap["The Beatles"]}")
            .header(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody()
            .jsonPath("$.message").isNotEmpty
            .jsonPath("$.message").isEqualTo("An unexpected error occurred")
    }
}


package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.domain.UserRole
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import java.time.Instant

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@AutoConfigureWebTestClient
class ArtistAdminControllerTest(
    @Autowired val client: WebTestClient,
    @Autowired val userRepository: UserRepository,
    @Autowired val songRepository: SongRepository,
    @Autowired val artistRepository: ArtistRepository,
    @Autowired val encoder: PasswordEncoder
) {

    private val adminUser: String = "admin"
    private val adminPassword: String = "secret"
    private val adminRole: String = "ADMIN"
    private val ownerUser: String = "owner"
    private val ownerPassword: String = "verysecret"
    private val ownerRole: String = "OWNER"
    private lateinit var artistMap: Map<String, Long>

    @BeforeEach
    fun createUser() {
        songRepository.deleteAll()
        artistRepository.deleteAll()
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
        val artist = artistRepository.save(
            Artist(
                name = "The Beatles",
                flickrPhotos = mutableSetOf(
                    ArtistFlickrPhoto("1"), ArtistFlickrPhoto("2")
                ),
                wikimediaPhotos = mutableSetOf(
                    ArtistWikimediaPhoto(
                        url = "https://upload.wikimedia.org/wikipedia/commons/6/61/The_Beatles_arrive_at_JFK_Airport.jpg",
                        attribution = "United Press International, Public domain, via Wikimedia Commons"
                    )
                ),
                logEntries = mutableListOf(
                    ArtistLogEntry(
                        date = Instant.now(),
                        username = "Temp"
                    )
                )
            )
        )
        artistMap = artistRepository.saveAll(
            listOf(
                artist
            )
        ).map { it.name to it.id!! }.toMap()
    }

    @Test
    fun getArtistsByNameTest() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/admin/artists")
                    .queryParam("name", "The Beatles")
                    .build()
            }
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(adminUser, adminPassword) }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<AdminArtistDto>().hasSize(1)
    }

    @Test
    fun deleteArtistByIdTest() {
        client.delete()
            .uri("/admin/artists/${artistMap["The Beatles"]}")
            .headers { httpHeadersConsumer -> httpHeadersConsumer.setBasicAuth(ownerUser, ownerPassword) }
            .exchange()
            .expectStatus().isOk
        assertFalse(artistRepository.findById(artistMap["The Beatles"]!!).isPresent)
    }
}


package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.Jsonb
import nl.orangeflamingo.voornameninliedjesbackend.domain.OperationType
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import java.net.URI
import java.time.Instant

class ArtistAdminControllerIT : AbstractIntegrationTest() {

    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var songRepository: SongRepository

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    private lateinit var artistMap: Map<String, Long>
    private lateinit var adminToken: String
    private lateinit var ownerToken: String

    @BeforeEach
    fun createUser() {
        songRepository.deleteAll()
        artistRepository.deleteAll()
        val artist = artistRepository.save(
            Artist(
                name = "The Beatles",
                photos = mutableSetOf(
                    ArtistPhoto(
                        url = URI.create("https://upload.wikimedia.org/wikipedia/commons/6/61/The_Beatles_arrive_at_JFK_Airport.jpg"),
                        attribution = "United Press International, Public domain, via Wikimedia Commons"
                    )
                ),
                logEntries = mutableSetOf(
                    ArtistLogEntry(
                        date = Instant.now(),
                        username = "Temp",
                        userId = "user-id",
                        httpMethod = OperationType.CREATE,
                        request = Jsonb("""{"name":"The Police","background":"English rock band"}""")
                    )
                )
            )
        )
        artistMap = artistRepository.saveAll(
            listOf(
                artist
            )
        ).associate { it.name to it.id!! }

        adminToken = getAdminToken()
        ownerToken = getOwnerToken()
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
            .headers { it.setBearerAuth(adminToken) }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<AdminArtistDto>().hasSize(1)
    }

    @Test
    fun deleteArtistByIdTest() {
        client.delete()
            .uri("/admin/artists/${artistMap["The Beatles"]}")
            .headers { it.setBearerAuth(ownerToken) }
            .exchange()
            .expectStatus().isOk
        assertThat(artistRepository.findById(artistMap["The Beatles"]!!).isPresent).isFalse
    }

    @Test
    fun updateArtistNameTest() {
        client.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/admin/artists/${artistMap["The Beatles"]}")
                    .queryParam("name", "The Beat")
                    .build()
            }
            .headers { it.setBearerAuth(adminToken) }
            .exchange()
            .expectStatus().isOk
        assertThat(artistRepository.findFirstByName("The Beat")).isNotNull
    }
}


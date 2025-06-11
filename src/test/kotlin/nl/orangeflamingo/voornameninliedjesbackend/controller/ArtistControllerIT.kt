package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.model.ArtistInputDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
import org.testcontainers.shaded.com.google.common.net.HttpHeaders
import java.net.URI

class ArtistControllerIT : AbstractIntegrationTest() {
    private lateinit var artistMap: Map<String, Long>
    @Autowired
    private lateinit var client: WebTestClient
    @Autowired
    private lateinit var songRepository: SongRepository
    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @BeforeEach
    fun createUser() {
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
    fun deleteArtistByIdTestV2() {
        client.delete()
            .uri("/api/artists/${artistMap["The Beatles"]}")
            .header(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun createArtistTestV2() {
        client.post()
            .uri("/api/artists")
            .header(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
            .body(
                BodyInserters.fromValue(
                    ArtistInputDto("newArtist", emptyList())
                )
            )
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun createArtistWithExistingNameTestV2() {
        client.post()
            .uri("/api/artists")
            .header(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
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
            .header(HttpHeaders.ACCEPT, "application/vnd.voornameninliedjes.artists.v2+json")
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
}


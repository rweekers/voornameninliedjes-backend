package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

class ArtistControllerTest : AbstractIntegrationTest() {
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
            flickrPhotos = mutableSetOf(ArtistFlickrPhoto(1, "1"), ArtistFlickrPhoto(2, "2")),
            wikimediaPhotos = mutableSetOf(
                ArtistWikimediaPhoto(
                    url = "https://upload.wikimedia.org/wikipedia/commons/6/61/The_Beatles_arrive_at_JFK_Airport.jpg",
                    attribution = "United Press International, Public domain, via Wikimedia Commons"
                )
            )
        )
        artistMap = artistRepository.saveAll(
            listOf(
                artist,
                artist.copy(
                    name = "The Rolling Stones",
                    flickrPhotos = mutableSetOf(),
                    wikimediaPhotos = mutableSetOf()
                )
            )
        ).associate { it.name to it.id!! }
    }

    @Test
    fun getAllArtistsTest() {
        client.get()
            .uri("/api/artists")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<ArtistDto>().hasSize(2)
    }

    @Test
    fun getArtistsByNameTest() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/artists")
                    .queryParam("name", "The Rolling Stones")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<ArtistDto>().hasSize(1)
    }

    @Test
    fun getArtistByIdTest() {
        client.get()
            .uri("/api/artists/${artistMap["The Beatles"]}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isNotEmpty
            .jsonPath("$.name").isEqualTo("The Beatles")
            .jsonPath("$.flickrPhotos").isNotEmpty
            .jsonPath("$.flickrPhotos[0].flickrId").isEqualTo("1")
    }
}


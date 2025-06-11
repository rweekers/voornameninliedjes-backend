package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongSource
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.dto.TestSongDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import java.net.URI

class SongControllerIT : AbstractIntegrationTest() {
    @Autowired
    private lateinit var client: WebTestClient
    @Autowired
    private lateinit var songRepository: SongRepository
    @Autowired
    private lateinit var artistRepository: ArtistRepository
    @Autowired
    private lateinit var songController: SongController
    private lateinit var songMap: Map<String, Long>

    @BeforeEach
    fun createSongs() {
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
                )
            )
        )

        val songMichelle = Song(
            title = "Michelle",
            name = "Michelle",
            status = SongStatus.SHOW,
            wikipediaPage = "wikiPageMichelle",
            photos = mutableSetOf(
                SongPhoto(
                    id = 1,
                    url = "https://somefakewikimediaphotourl.doesnotexist",
                    attribution = "Attribution for test wikimedia photo"
                )
            ),
            artist = AggregateReference.to(artist.id ?: throw IllegalStateException()),
            sources = setOf(
                SongSource(
                    id = 1,
                    url = "https://nl.wikipedia.org/wiki/Michelle_(lied)",
                    name = "Wikipedia Pagina"
                )
            )
        )

        songMap = songRepository.saveAll(
            listOf(
                songMichelle, songMichelle.copy(
                    title = "Lucy in the Sky with Diamonds", name = "Lucy", photos = mutableSetOf(
                        SongPhoto(
                            id = 2,
                            url = "https://somefakewikimediaphotourl.doesnotexist",
                            attribution = "Attribution for test wikimedia photo"
                        )
                    ), sources = setOf(
                        SongSource(
                            id = 2,
                            url = "https://nl.wikipedia.org/wiki/Lucy_in_the_Sky_with_Diamonds",
                            name = "Wikipedia Pagina"
                        )
                    )
                )
            )
        ).associate { it.title to it.id!! }
    }

    @Test
    fun getAllSongsTest() {
        client.get()
            .uri("/api/songs")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TestSongDto>().hasSize(2)
    }

    @Test
    fun getAllSongsStartingWithTest() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/songs")
                    .queryParam("first-characters", "m")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TestSongDto>().hasSize(1)
    }

    @Test
    fun getAllSongsStartingCharactersWithTest() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/songs")
                    .queryParam("first-character", "mi")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TestSongDto>().hasSize(1)
    }

    @Test
    fun getSongByArtistAndTitleTest() {
        client.get()
            .uri("/api/songs/the Beatles/MICHELLE")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isNotEmpty
            .jsonPath("$.title").isEqualTo("Michelle")
            .jsonPath("$.artist").isNotEmpty
            .jsonPath("$.artist").isEqualTo("The Beatles")
    }

    @Test
    fun getNotExistingSongByArtistAndTitleTest() {
        client.get()
            .uri("/api/songs/no artist/no song")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun getStatisticsPerSongName() {
        client.get()
            .uri("/api/song-name-statistics")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isNotEmpty
            .jsonPath("$[0].name").isEqualTo("Lucy")
            .jsonPath("$[0].count").isEqualTo("1")
    }

    @Test
    fun getStatisticsPerArtistName() {
        client.get()
            .uri("/api/artist-name-statistics")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isNotEmpty
            .jsonPath("$[0].name").isEqualTo("The Beatles")
            .jsonPath("$[0].count").isEqualTo("2")
    }

    @Test
    fun getSongCount() {
        client.get()
            .uri("/api/songs/count")
            .exchange()
            .expectStatus().isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.count").isEqualTo(2)
    }

    @Test
    fun getSongStatistics() {
        client.get()
            .uri("/api/songs/statistics")
            .exchange()
            .expectStatus().isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBodyList<TestSongDto>().hasSize(1)
    }
}


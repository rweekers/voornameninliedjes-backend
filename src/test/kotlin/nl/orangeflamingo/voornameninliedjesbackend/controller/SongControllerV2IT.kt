package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongSource
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.dto.TestSongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.TestSongPageDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.net.URI

class SongControllerV2IT: AbstractIntegrationTest() {

    @Autowired
    private lateinit var client: WebTestClient
    @Autowired
    private lateinit var songRepository: SongRepository
    @Autowired
    private lateinit var artistRepository: ArtistRepository
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
            .header("Accept", "application/vnd.voornameninliedjes.songs.v2+json")
            .exchange()
            .expectStatus().isOk
            .expectBody<TestSongPageDto>()
            .consumeWith { response ->
                val body = response.responseBody
                assertThat(body).isNotNull
                assertThat(body?.songs).hasSize(2)
                assertThat(body?.totalItems).isEqualTo(2)
                assertThat(body?.isLast).isEqualTo(true)
            }
    }

    @Test
    fun getAllSongsStartingWithTest() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/songs")
                    .queryParam("name-starts-with", "m")
                    .build()
            }
            .header("Accept", "application/vnd.voornameninliedjes.songs.v2+json")
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
                    .queryParam("name-starts-with", "mi")
                    .build()
            }
            .header("Accept", "application/vnd.voornameninliedjes.songs.v2+json")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TestSongDto>().hasSize(1)
    }
}
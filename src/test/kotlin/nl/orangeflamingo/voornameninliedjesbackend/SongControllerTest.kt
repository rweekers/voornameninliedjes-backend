package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.dto.TestSongDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@AutoConfigureWebTestClient
class SongControllerTest(
    @Autowired val client: WebTestClient,
    @Autowired val songRepository: SongRepository,
    @Autowired val artistRepository: ArtistRepository
) {
    private lateinit var songMap: Map<String, Long>

    @BeforeEach
    fun createUser() {
        songRepository.deleteAll()
        artistRepository.deleteAll()

        val artist = artistRepository.save(
            Artist(
                name = "The Beatles",
                flickrPhotos = mutableSetOf(ArtistFlickrPhoto("1"), ArtistFlickrPhoto("2")),
                wikimediaPhotos = mutableSetOf(
                    ArtistWikimediaPhoto(
                        url = "https://upload.wikimedia.org/wikipedia/commons/6/61/The_Beatles_arrive_at_JFK_Airport.jpg",
                        attribution = "United Press International, Public domain, via Wikimedia Commons"
                    )
                )
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
            ),
            sources = listOf(
                SongSource(
                    url = "https://nl.wikipedia.org/wiki/Michelle_(lied)",
                    name = "Wikipedia Pagina"
                )
            )
        )

        songMap = songRepository.saveAll(
            listOf(
                songMichelle, songMichelle.copy(
                    title = "Lucy in the Sky with Diamonds", name = "Lucy", sources = listOf(
                        SongSource(
                            url = "https://nl.wikipedia.org/wiki/Lucy_in_the_Sky_with_Diamonds",
                            name = "Wikipedia Pagina"
                        )
                    )
                )
            )
        ).map { it.title to it.id!! }.toMap()
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
    fun getSongByIdTest() {
        client.get()
            .uri("/api/songs/${songMap["Michelle"]}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.title").isNotEmpty
            .jsonPath("$.title").isEqualTo("Michelle")
            .jsonPath("$.artist").isNotEmpty
            .jsonPath("$.artist").isEqualTo("The Beatles")
            .jsonPath("$.flickrPhotos").isNotEmpty
            .jsonPath("$.flickrPhotos[0].url").isEqualTo("https://somefakeflickrphotourl.doesnotexist")
    }
}


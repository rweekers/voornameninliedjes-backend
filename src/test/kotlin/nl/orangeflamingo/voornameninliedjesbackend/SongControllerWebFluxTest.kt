package nl.orangeflamingo.voornameninliedjesbackend

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.SongRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import reactor.core.publisher.Flux

@WebFluxTest
class SongControllerWebFluxTest(@Autowired val client: WebTestClient) {

    @MockkBean
    private lateinit var repository: SongRepository

    private val testSong = Song(
            id = "1",
            artist = "Paul Simon",
            title = "You Can Call Me Al",
            name = "Al",
            background = "Some background text",
            spotify = "",
            youtube = "",
            status = SongStatus.SHOW,
            flickrPhotos = emptySet(),
            wikimediaPhotos = emptySet()
    )

    @Test
    fun allSongsTest() {
        every { repository.findAllByStatusOrderByName(SongStatus.SHOW) } returns Flux.just(testSong)
        client.get().uri("/api/songs").exchange()
                .expectStatus().isOk
                .expectBodyList<Song>().hasSize(1).contains(testSong)
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.steps

import io.cucumber.java8.En
import nl.orangeflamingo.voornameninliedjesbackend.controller.SongController
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@Suppress("SpringJavaAutowiredMembersInspection")
class SongSteps : En {

    private val log = LoggerFactory.getLogger(SongSteps::class.java)

    @Autowired
    private lateinit var songController: SongController

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var songRepository: SongRepository

    init {
        Given("the next song for artist {string}:") { artistName: String, song: Song ->
            log.info("Song: $song")
            val artist = artistRepository.findFirstByName(artistName) ?: Artist(name = artistName)
            song.addArtist(artist)
            songRepository.save(song)
        }

        Then("there are {int} songs returned") { numberOfSongs: Int ->
            val songsCount = songController.getSongs().size
            assertEquals(numberOfSongs, songsCount)
        }

        DataTableType { entry: Map<String, String> ->
            Song(
                title = entry["title"] ?: throw IllegalArgumentException("There should be a title"),
                name = entry["name"] ?: throw IllegalArgumentException("There should be a name"),
                status = SongStatus.valueOf(
                    entry["status"] ?: throw IllegalArgumentException("There should be a title")
                ),
                artists = mutableSetOf()
            )
        }
    }
}
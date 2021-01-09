package nl.orangeflamingo.voornameninliedjesbackend.steps

import io.cucumber.java8.En
import nl.orangeflamingo.voornameninliedjesbackend.controller.SongController
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.Assertions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class SongSteps : En {

    private val log = LoggerFactory.getLogger(SongSteps::class.java)

    @Autowired
    private lateinit var songController: SongController

    @Autowired
    private lateinit var songRepository: SongRepository

    init {
        Given("the next song:") { song: Song ->
            log.info("Song: $song")
            songRepository.save(song)
        }

        Then("there are {int} songs returned") { numberOfSongs: Int ->
            val songs = songController.getSongs().size
            Assertions.assertEquals(numberOfSongs, songs)
        }

        DataTableType { entry: Map<String, String> ->
            Song(
                title = entry["title"] ?: throw RuntimeException(),
                name = entry["name"] ?: throw RuntimeException(),
                status = SongStatus.valueOf(entry["status"] ?: throw RuntimeException()),
                artists = mutableSetOf()
            )
        }
    }
}
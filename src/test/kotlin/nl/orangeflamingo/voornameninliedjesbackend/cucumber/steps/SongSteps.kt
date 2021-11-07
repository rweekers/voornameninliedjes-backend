package nl.orangeflamingo.voornameninliedjesbackend.cucumber.steps

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import io.cucumber.java8.En
import nl.orangeflamingo.voornameninliedjesbackend.controller.SongController
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.junit.jupiter.api.Assertions.assertEquals
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Suppress("SpringJavaAutowiredMembersInspection")
class SongSteps : En {

    private val log = LoggerFactory.getLogger(SongSteps::class.java)
    private val klaxon = Klaxon()
    private val parser = Parser.default()

    @Autowired
    private lateinit var songService: SongService

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

        When("user {word} updates the song {string} with the following details:") { user: String, songTitle: String, aggregateSong: AggregateSong ->
            val song = songRepository.findFirstByTitle(songTitle).orElseThrow()
            songService.updateSong(aggregateSong = aggregateSong, song = song, user = user)
        }

        Then("there are {int} songs returned") { numberOfSongs: Int ->
            val songsCount = songController.getSongs(Optional.empty()).size
            assertEquals(numberOfSongs, songsCount)
        }

        DataTableType { entry: Map<String, String> ->
            val testSong = TestSong()
            val jsonObject = parser.parse(StringBuilder(klaxon.toJsonString(testSong))) as JsonObject
            entry.forEach { (key, value) -> jsonObject[key] = value }

            val updatedTestSong = klaxon.maybeParse<TestSong>(jsonObject)!!
            updatedTestSong.toDomain()
        }

        DataTableType { entry: Map<String, String> ->
            val testAggregateSong = TestAggregateSong()
            val jsonObject = parser.parse(StringBuilder(klaxon.toJsonString(testAggregateSong))) as JsonObject
            entry.forEach { (key, value) -> jsonObject[key] = value }

            val updatedTestAggregateSong = klaxon.maybeParse<TestAggregateSong>(jsonObject)!!
            updatedTestAggregateSong.toDomain()
        }
    }
}
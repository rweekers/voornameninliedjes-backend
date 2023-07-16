package nl.orangeflamingo.voornameninliedjesbackend.cucumber.steps

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import nl.orangeflamingo.voornameninliedjesbackend.controller.SongController
import nl.orangeflamingo.voornameninliedjesbackend.domain.AggregateSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.TestAggregateSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.TestSong
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.junit.jupiter.api.Assertions.assertEquals
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jdbc.core.mapping.AggregateReference
import java.util.Optional

@Suppress("SpringJavaAutowiredMembersInspection")
class SongSteps {

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

    @Given("the next song for artist {string}:")
    fun givenSongXForArtistY(artistName: String, song: Song) {
        log.info("Song: $song")
        val artist = artistRepository.findFirstByName(artistName) ?: Artist(name = artistName)
        song.artist = AggregateReference.to(artist.id ?: throw IllegalStateException())
        songRepository.save(song)
    }

    @When("user {word} updates the song {string} with the following details:")
    fun whenUserXUpdatesSongYWithDetailsZ(user: String, songTitle: String, aggregateSong: AggregateSong) {
        val song = songRepository.findFirstByTitle(songTitle).orElseThrow()
        songService.updateSong(aggregateSong = aggregateSong, song = song, user = user)
    }

    @Then("there are {int} songs returned")
    fun thenThereAreXSongsReturned(numberOfSongs: Int) {
        val songsCount = songController.getSongs(Optional.empty()).size
        assertEquals(numberOfSongs, songsCount)
    }

    @DataTableType
    fun song(entry: Map<String, String>): Song {
        val testSong = TestSong()
        val jsonObject = parser.parse(StringBuilder(klaxon.toJsonString(testSong))) as JsonObject
        entry.forEach { (key, value) -> jsonObject[key] = value }

        val updatedTestSong = klaxon.maybeParse<TestSong>(jsonObject)!!
        return updatedTestSong.toDomain()
    }

    @DataTableType
    fun aggregateSong(entry: Map<String, String>): AggregateSong {
        val testAggregateSong = TestAggregateSong()
        val jsonObject = parser.parse(StringBuilder(klaxon.toJsonString(testAggregateSong))) as JsonObject
        entry.forEach { (key, value) -> jsonObject[key] = value }

        val updatedTestAggregateSong = klaxon.maybeParse<TestAggregateSong>(jsonObject)!!
        return updatedTestAggregateSong.toDomain()
    }
}
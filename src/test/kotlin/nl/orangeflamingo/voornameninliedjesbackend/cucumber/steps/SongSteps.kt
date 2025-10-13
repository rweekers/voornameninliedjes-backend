package nl.orangeflamingo.voornameninliedjesbackend.cucumber.steps

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import nl.orangeflamingo.voornameninliedjesbackend.controller.SongController
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.junit.jupiter.api.Assertions.assertEquals
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jdbc.core.mapping.AggregateReference
import java.util.*

@Suppress("SpringJavaAutowiredMembersInspection")
class SongSteps {

    private val log = LoggerFactory.getLogger(SongSteps::class.java)
    private val mapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())

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
        return mapper.convertValue(entry, TestSong::class.java).toDomain()
    }

    @DataTableType
    fun aggregateSong(entry: Map<String, String>): AggregateSong {
        return mapper.convertValue(entry, TestAggregateSong::class.java).toDomain()
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.steps

import io.cucumber.java8.En
import nl.orangeflamingo.voornameninliedjesbackend.controller.SongController
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.junit.jupiter.api.Assertions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class ArtistSteps: En {

    private val log = LoggerFactory.getLogger(ArtistSteps::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    init {
        Given("the artist {string}") { artistName: String ->
            val artist = Artist(
                name = artistName
            )
            artistRepository.save(artist)
        }
    }
}
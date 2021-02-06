@file:Suppress("SpringJavaAutowiredMembersInspection")

package nl.orangeflamingo.voornameninliedjesbackend.cucumber.steps

import io.cucumber.java8.En
import nl.orangeflamingo.voornameninliedjesbackend.controller.ArtistController
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@Suppress("SpringJavaAutowiredMembersInspection")
class ArtistSteps: En {

    private val log = LoggerFactory.getLogger(ArtistSteps::class.java)

    @Autowired
    private lateinit var artistController: ArtistController

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    init {
        Given("the artist {string}") { artistName: String ->
            val artist = Artist(
                name = artistName
            )
            log.info("Persisting $artist")
            artistRepository.save(artist)
        }


        Then("there are {int} artists returned") { numberOfSongs: Int ->
            val artistsCount = artistController.getArtists().size
            assertEquals(numberOfSongs, artistsCount)
        }

        Then("there are {int} artists with name {string}") { numberOfArtists: Int, artistName: String ->
            val artistsCount = artistController.getArtistsByName(artistName).size
            assertEquals(numberOfArtists, artistsCount)
        }
    }
}
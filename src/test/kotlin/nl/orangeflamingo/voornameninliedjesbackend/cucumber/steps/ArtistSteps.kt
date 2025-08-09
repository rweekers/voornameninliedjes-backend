@file:Suppress("SpringJavaAutowiredMembersInspection")

package nl.orangeflamingo.voornameninliedjesbackend.cucumber.steps

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import nl.orangeflamingo.voornameninliedjesbackend.controller.ArtistController
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.assertj.core.api.Assertions.assertThat
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@Suppress("SpringJavaAutowiredMembersInspection")
class ArtistSteps {

    private val log = LoggerFactory.getLogger(ArtistSteps::class.java)

    @Autowired
    private lateinit var artistController: ArtistController

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Given("the artist {string}")
    fun givenArtist(artistName: String) {
            val artist = Artist(
                name = artistName
            )
            log.info("Persisting $artist")
            artistRepository.save(artist)
        }

    @Then("there are {int} artists returned")
     fun thereAreXArtistsReturned(numberOfArtists: Int) {
            val artists = artistController.getArtists()
            assertThat(artists).hasSize(numberOfArtists)
        }

    @Then("there are {int} artists with name {string}")
    fun thereAreXArtistsWithNameY(numberOfArtists: Int, artistName: String) {
        val artists = artistRepository.findByNameIgnoreCase(artistName)
        assertThat(artists).hasSize(numberOfArtists)
    }
}
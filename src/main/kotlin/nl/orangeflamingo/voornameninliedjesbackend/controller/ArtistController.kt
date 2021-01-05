package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/beta")
class ArtistController {

    private val log = LoggerFactory.getLogger(ArtistController::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @GetMapping("/artists/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getArtistById(@PathVariable id: Long): Optional<Artist> {
        log.info("Requesting artist with id $id...")
        return artistRepository.findById(id)
    }

    @GetMapping("/artists", params = ["name"])
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun getArtistsByName(@RequestParam(name = "name") name: String): List<Artist> {
        return artistRepository.findByName(name)
    }

    @GetMapping("/artists")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getArtists(): List<Artist> {
        log.info("Requesting all artists...")
        return artistRepository.findAllOrderedByName()
    }
}
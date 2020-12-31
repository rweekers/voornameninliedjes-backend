package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/beta")
class ArtistController {

    private val log = LoggerFactory.getLogger(ArtistController::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var artistService: ArtistService

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/artists/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getArtistById(@PathVariable id: Long): Optional<Artist> {
        log.info("Requesting artist with id $id...")
        return artistRepository.findById(id)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/artists", params = ["name"])
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun getArtistsByName(@RequestParam(name = "name") name: String): List<Artist> {
        return artistRepository.findByName(name)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/artists")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getArtists(): List<Artist> {
        log.info("Requesting all artists...")
        return artistRepository.findAllOrderedByName()
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/artists")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun deleteArtists() {
        val count = artistService.countArtists()
        artistRepository.deleteAll()
        log.info("$count artists deleted")
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/artists/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun deleteArtistById(@PathVariable id: Long) {
        artistRepository.deleteById(id)
        log.info("artist $id deleted")
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/gamma")
class ArtistAdminController {

    private val log = LoggerFactory.getLogger(ArtistAdminController::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var artistService: ArtistService

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
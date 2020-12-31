package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/beta")
class SongController {

    private val log = LoggerFactory.getLogger(SongController::class.java)

    @Autowired
    private lateinit var songRepository: SongRepository

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getSongById(@PathVariable id: Long): Optional<Song> {
        log.info("Requesting song with id $id...")
        return songRepository.findById(id)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getSongs(): List<Song> {
        log.info("Requesting all songs...")
        return songRepository.findAllOrderedByName()
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun deleteSongs() {
        val count = songRepository.count()
        songRepository.deleteAll()
        log.info("$count songs deleted")
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun deleteSongById(@PathVariable id: Long) {
        songRepository.deleteById(id)
        log.info("song $id deleted")
    }
}
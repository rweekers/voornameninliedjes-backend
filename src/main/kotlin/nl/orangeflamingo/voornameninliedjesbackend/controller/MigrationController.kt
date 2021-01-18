package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class MigrationController {

    private val log = LoggerFactory.getLogger(MigrationController::class.java)

    @Autowired
    private lateinit var songService: SongService

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PostMapping("/songs/migrate")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun migrateSongs() {
        val count = songService.countSongs()
        log.info("There are $count songs")
        if (count > 0) {
            log.info("Not migrating songs, already songs present")
            return
        }
        songService.migrateSongs()
        log.info("Songs migrated")
    }
}
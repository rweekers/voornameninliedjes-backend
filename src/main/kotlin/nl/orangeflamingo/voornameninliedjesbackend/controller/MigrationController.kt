package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/beta")
class MigrationController {

    private val log = LoggerFactory.getLogger(MigrationController::class.java)

    @Autowired
    private lateinit var artistService: ArtistService

    @Autowired
    private lateinit var songService: SongService

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PostMapping("/artists/migrate")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun migrateArtists() {
        val count = artistService.countArtists()
        log.info("There are $count artists")
        if (count > 0) {
            log.info("Not migrating artists, already artists present")
            return
        }
        artistService.migrateArtists()
        log.info("There are ${artistService.countArtists()} artists migrated")
    }

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
        log.info("$count songs migrated")
    }
}
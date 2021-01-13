package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CachePut
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/gamma")
class SongAdminController {

    private val log = LoggerFactory.getLogger(SongAdminController::class.java)

    @Autowired
    private lateinit var songRepository: SongRepository

    @Autowired
    private lateinit var songService: SongService

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs")
    @CachePut(value = ["songs"])
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun getSongs(): List<AdminSongDto> {
        return songService.findAll()
            .map { convertToDto(it) }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun getSongById(@PathVariable("id") id: Long): AdminSongDto {
        return convertToDto(songService.findById(id))
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

    private fun convertToDto(song: AggregateSong): AdminSongDto {
        return AdminSongDto(
            id = song.id.toString(),
            artist = song.artistName,
            title = song.title,
            name = song.name,
            artistImage = song.artistImage,
            background = song.background,
            youtube = song.youtube,
            spotify = song.spotify,
            status = song.status.name,
            wikimediaPhotos = song.wikimediaPhotos.map { w -> convertToDto(w) }.toSet(),
            flickrPhotos = song.flickrPhotos.map { it.flickrId }.toSet(),
            sources = song.sources.map { s -> convertToDto(s) }.toSet(),
            logs = song.logEntries.map { convertToDto(it) }
        )
    }

    private fun convertToDto(it: SongLogEntry): AdminLogEntry {
        return AdminLogEntry(
            date = it.date,
            user = it.username
        )
    }

    private fun convertToDto(wikimediaPhoto: ArtistWikimediaPhoto): AdminWikimediaPhotoDto {
        return AdminWikimediaPhotoDto(
            url = wikimediaPhoto.url,
            attribution = wikimediaPhoto.attribution
        )
    }

    private fun convertToDto(songSource: SongSource): AdminSourceDto {
        return AdminSourceDto(
            url = songSource.url,
            name = songSource.name
        )
    }
}
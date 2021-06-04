package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSourceDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminWikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CachePut
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/admin")
class SongAdminController {

    private val log = LoggerFactory.getLogger(SongAdminController::class.java)

    @Autowired
    private lateinit var songRepository: SongRepository

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var songService: SongService

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs")
    @CachePut(value = ["songs"])
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun getSongs(): List<AdminSongDto> {
        return songService.findAll()
            .map { convertToDto(it) }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs", params = ["name"])
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun getSongsByName(@RequestParam(name = "name") name: String): List<AdminSongDto> {
        return songService.findByName(name)
            .map { convertToDto(it) }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs", params = ["first-character"])
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun getSongsWithNameStartingWith(@RequestParam(name = "first-character") firstCharacter: String, @RequestParam(name="status", defaultValue = "") status: List<String> = emptyList()): List<AdminSongDto> {
        return songService.findByNameStartsWithAndStatusIn(firstCharacter, status.map { SongStatus.valueOf(it) })
            .map { convertToDto(it) }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun getSongById(@PathVariable("id") id: Long): AdminSongDto {
        return convertToDto(songService.findById(id))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/{user}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun newSong(@RequestBody newSong: AdminSongDto, @PathVariable user: String): AdminSongDto {
        log.info("Saving song with title ${newSong.title} and artist ${newSong.artist}")
        val savedSong = songService.newSong(convertToDomain(newSong), user)
        log.info("Saved song, id is ${savedSong.id}")
        return convertToDto(savedSong)
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/songs/{user}/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun replaceSong(@RequestBody song: AdminSongDto, @PathVariable user: String, @PathVariable id: Long): AdminSongDto {
        assert(song.id?.toLong() == id)
        val songFromDb = songRepository.findById(id)

        if (songFromDb.isPresent) {
            return convertToDto(songService.updateSong(convertToDomain(song), songFromDb.get(), user))
        }
        return convertToDto(songService.newSong(convertToDomain(song), user))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/{user}/{id}/{flickrId}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun addFlickrPhotoForSong(@PathVariable user: String, @PathVariable id: Long, @PathVariable flickrId: String) {
        val songOptional = songRepository.findById(id)
        if (songOptional.isPresent) {
            val song = songOptional.get()
            val artist = findLeadArtistForSong(song)
                ?: throw IllegalStateException("There should be a lead artist for all songs")
            addFlickrIdToArtist(user, artist, flickrId)
        } else {
            log.warn("Song with id $id not found")
        }
    }

    private fun findLeadArtistForSong(song: Song): Artist? {
        return song.artists
            .filter { artistRef -> artistRef.originalArtist }
            .map { artistRepository.findById(it.artist) }
            .first().orElseThrow()
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/artists/{user}/{id}/{flickrId}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun addFlickrPhoto(@PathVariable user: String, @PathVariable id: Long, @PathVariable flickrId: String) {
        val artist = artistRepository.findById(id).orElseThrow()
        addFlickrIdToArtist(user, artist, flickrId)
    }

    private fun addFlickrIdToArtist(
        user: String,
        artist: Artist,
        flickrId: String
    ) {
        val logEntry = ArtistLogEntry(Instant.now(), user)
        artist.logEntries.add(logEntry)
        artist.flickrPhotos.add(ArtistFlickrPhoto(flickrId))
        artistRepository.save(artist)
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun deleteSongs() {
        val count = songRepository.count()
        songRepository.updateAllSongStatus(SongStatus.TO_BE_DELETED)
        log.info("$count songs marked as to be deleted")
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun deleteSongById(@PathVariable id: Long) {
        songRepository.deleteById(id)
        log.info("song $id deleted")
    }

    private fun convertToDomain(song: AdminSongDto): AggregateSong {
        return AggregateSong(
            id = song.id?.toLong(),
            title = song.title.trim(),
            name = song.name.trim(),
            artistName = song.artist.trim(),
            artistImage = song.artistImage?.trim(),
            background = song.background?.trim(),
            wikipediaPage = song.wikipediaPage?.trim(),
            youtube = song.youtube?.trim(),
            spotify = song.spotify?.trim(),
            status = SongStatus.valueOf(song.status.trim()),
            wikimediaPhotos = song.wikimediaPhotos.map {
                ArtistWikimediaPhoto(
                    url = it.url.trim(),
                    attribution = it.attribution.trim()
                )
            }.toSet(),
            flickrPhotos = song.flickrPhotos.map { ArtistFlickrPhoto(it.trim()) }.toSet(),
            sources = song.sources.map { SongSource(it.url.trim(), it.name.trim()) },
            logEntries = song.logs.map { SongLogEntry(it.date, it.user.trim()) }.toMutableList()
        )

    }

    private fun convertToDto(song: AggregateSong): AdminSongDto {
        return AdminSongDto(
            id = song.id.toString(),
            artist = song.artistName,
            title = song.title,
            name = song.name,
            artistImage = if (!song.artistImage.isNullOrEmpty()) song.artistImage else "https://ak9.picdn.net/shutterstock/videos/24149239/thumb/1.jpg",
            background = song.background,
            wikipediaPage = song.wikipediaPage,
            youtube = song.youtube,
            spotify = song.spotify,
            status = song.status.code,
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
package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSourceDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminWikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/admin")
class SongAdminController(
    private val songRepository: SongRepository,
    private val songService: SongService,
    private val wikipediaEnrichmentService: WikipediaEnrichmentService,
    private val lastFmEnrichmentService: LastFmEnrichmentService
) {

    private val log = LoggerFactory.getLogger(SongAdminController::class.java)

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(SongNotFoundException::class, ArtistNotFoundException::class)
    fun notFoundHandler(ex: NotFoundException) {
        log.warn("Gotten request with message {}", ex.message)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = ["/songs", "/songs/"])
    fun getSongs(): List<AdminSongDto> {
        return songService.findAll()
            .map { convertToDto(it) }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs", params = ["name"])
    fun getSongsByName(@RequestParam(name = "name") name: String): List<AdminSongDto> {
        return songService.findByName(name)
            .map { convertToDto(it) }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs", params = ["first-character"])
    fun getSongsWithNameStartingWith(
        @RequestParam(name = "first-character") firstCharacter: String,
        @RequestParam(
            name = "status",
            defaultValue = ""
        ) status: List<String> = emptyList()
    ): List<AdminSongDto> {
        return songService.findByNameStartsWithAndStatusIn(firstCharacter, status.map { SongStatus.valueOf(it) })
            .map { convertToDto(it) }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/songs/{id}")
    fun getSongById(@PathVariable("id") id: Long): AdminSongDto {
        return convertToDto(songService.findById(id))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/{user}")
    fun newSong(@RequestBody newSong: AdminSongDto, @PathVariable user: String): AdminSongDto {
        log.info("Saving song with title ${newSong.title} and artist ${newSong.artist}")
        val savedSong = songService.newSong(convertToDomain(newSong), user)
        log.info("Saved song, id is ${savedSong.id}")
        return convertToDto(savedSong)
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/songs/{user}/{id}")
    fun replaceSong(@RequestBody song: AdminSongDto, @PathVariable user: String, @PathVariable id: Long): AdminSongDto {
        assert(song.id?.toLong() == id)
        val songFromDb = songRepository.findById(id)

        if (songFromDb.isPresent) {
            return convertToDto(songService.updateSong(convertToDomain(song), songFromDb.get(), user))
        }
        return convertToDto(songService.newSong(convertToDomain(song), user))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/enrich-wikipedia")
    fun enrichWikipediaForSongs(@RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean) {
        wikipediaEnrichmentService.enrichWikipediaForSongs(updateAll)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/enrich-lastfm")
    fun enrichLastFmInfoForSongs(@RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean) {
        lastFmEnrichmentService.enrichLastFmInfoForSongs(updateAll)
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/songs")
    fun deleteSongs() {
        val count = songRepository.count()
        songRepository.updateAllSongStatus(SongStatus.TO_BE_DELETED)
        log.info("$count songs marked as to be deleted")
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/songs/{id}")
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
            remarks = song.remarks?.trim(),
            hasDetails = song.hasDetails,
            artistPhotos = song.artistWikimediaPhotos.map {
                ArtistPhoto(
                    url = URI.create(it.url.trim()),
                    attribution = it.attribution.trim()
                )
            }.toSet(),
            songPhotos = song.songWikimediaPhotos.map {
                SongPhoto(
                    url = it.url.trim(),
                    attribution = it.attribution.trim()
                )
            }.toSet(),
            sources = song.sources.map { SongSource(url = it.url.trim(), name = it.name.trim()) }.toSet(),
            logEntries = song.logs.map { SongLogEntry(date = it.date, username = it.user.trim()) }.toMutableSet()
        )

    }

    private fun convertToDto(song: AggregateSong): AdminSongDto {
        return AdminSongDto(
            id = song.id.toString(),
            artist = song.artistName,
            title = song.title,
            name = song.name,
            artistImage = if (!song.artistImage.isNullOrEmpty()) song.artistImage else "https://ak9.picdn.net/shutterstock/videos/24149239/thumb/1.jpg",
            localImage = song.localImage,
            blurredImage = song.blurredImage,
            artistLastFmUrl = song.artistLastFmUrl,
            background = song.background,
            wikipediaPage = song.wikipediaPage,
            youtube = song.youtube,
            spotify = song.spotify,
            wikipediaNl = song.wikipediaContentNl,
            wikipediaEn = song.wikipediaContentEn,
            wikipediaSummaryEn = song.wikipediaSummaryEn,
            albumName = song.albumName,
            albumLastFmUrl = song.albumLastFmUrl,
            lastFmUrl = song.lastFmUrl,
            status = song.status.code,
            remarks = song.remarks,
            hasDetails = song.hasDetails,
            artistWikimediaPhotos = song.artistPhotos.map { convertToDto(it) },
            songWikimediaPhotos = song.songPhotos.map { convertToDto(it) },
            sources = song.sources.map { convertToDto(it) },
            tags = song.tags.map { convertToDto(it) },
            logs = song.logEntries.map { convertToDto(it) }
        )
    }

    private fun convertToDto(it: SongLogEntry): AdminLogEntry {
        return AdminLogEntry(
            date = it.date,
            user = it.username
        )
    }

    private fun convertToDto(wikimediaPhoto: ArtistPhoto): AdminWikimediaPhotoDto {
        return AdminWikimediaPhotoDto(
            url = wikimediaPhoto.url.toString(),
            attribution = wikimediaPhoto.attribution
        )
    }

    private fun convertToDto(wikimediaPhoto: SongPhoto): AdminWikimediaPhotoDto {
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

    private fun convertToDto(lastFmTag: SongLastFmTag): LastFmTagDto {
        return LastFmTagDto(
            url = lastFmTag.url,
            name = lastFmTag.name
        )
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.AggregateSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongSource
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminSourceDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminWikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistNotFoundException
import nl.orangeflamingo.voornameninliedjesbackend.service.ImagesEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.ImagesService
import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.NotFoundException
import nl.orangeflamingo.voornameninliedjesbackend.service.SongNotFoundException
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import nl.orangeflamingo.voornameninliedjesbackend.service.WikipediaEnrichmentService
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachePut
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/admin")
class SongAdminController(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val songService: SongService,
    private val imagesEnrichmentService: ImagesEnrichmentService,
    private val wikipediaEnrichmentService: WikipediaEnrichmentService,
    private val lastFmEnrichmentService: LastFmEnrichmentService,
    private val imagesService: ImagesService
) {

    private val log = LoggerFactory.getLogger(SongAdminController::class.java)

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(SongNotFoundException::class, ArtistNotFoundException::class)
    fun notFoundHandler(ex: NotFoundException) {
        log.warn("Gotten request with message {}", ex.message)
    }

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
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun getSongById(@PathVariable("id") id: Long): AdminSongDto {
        return convertToDto(songService.findById(id))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/{id}/download")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun downloadImageForSongById(
        @PathVariable("id") id: Long,
        @RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean
    ) {
        val song = songRepository.findById(id).orElseThrow { SongNotFoundException("Song with id $id not found") }
        imagesService.downloadImageForSong(song, updateAll)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/{id}/blur")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun blurImageForSongById(
        @PathVariable("id") id: Long,
        @RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean
    ) {
        val song = songRepository.findById(id).orElseThrow { SongNotFoundException("Song with id $id not found") }
        imagesService.blurImageForSong(song, updateAll)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/blur-all")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun blurImagesForSongs(
        @RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean
    ) {
        imagesService.blurImages(updateAll)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/download-all")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun downloadImagesForSongs(@RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean) {
        imagesService.downloadImages(updateAll)
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
    @PostMapping("/songs/{id}/enrich-images")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun enrichImageForSongById(@PathVariable("id") id: Long) {
        val song = songRepository.findById(id).orElseThrow { SongNotFoundException("Song with id $id not found") }
        imagesEnrichmentService.updateArtistImageForSong(song)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/enrich-images")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun enrichImagesForSongs(@RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean) {
        imagesEnrichmentService.enrichImagesForSongs(updateAll)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/enrich-wikipedia")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun enrichWikipediaForSongs(@RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean) {
        wikipediaEnrichmentService.enrichWikipediaForSongs(updateAll)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/songs/enrich-lastfm")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun enrichLastFmInfoForSongs(@RequestParam(name = "update-all", defaultValue = "false") updateAll: Boolean) {
        lastFmEnrichmentService.enrichLastFmInfoForSongs(updateAll)
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
            .first()
            .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/artists/{user}/{id}/{flickrId}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun addFlickrPhoto(@PathVariable user: String, @PathVariable id: Long, @PathVariable flickrId: String) {
        val artist =
            artistRepository.findById(id).orElseThrow { ArtistNotFoundException("Artist with id $id not found") }
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
            remarks = song.remarks?.trim(),
            hasDetails = song.hasDetails,
            artistWikimediaPhotos = song.artistWikimediaPhotos.map {
                ArtistWikimediaPhoto(
                    url = it.url.trim(),
                    attribution = it.attribution.trim()
                )
            }.toSet(),
            songWikimediaPhotos = song.songWikimediaPhotos.map {
                SongWikimediaPhoto(
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
            artistWikimediaPhotos = song.artistWikimediaPhotos.map { convertToDto(it) }.toSet(),
            songWikimediaPhotos = song.songWikimediaPhotos.map { convertToDto(it) }.toSet(),
            flickrPhotos = song.flickrPhotos.map { it.flickrId }.toSet(),
            sources = song.sources.map { convertToDto(it) }.toSet(),
            tags = song.tags.map { convertToDto(it) }.toSet(),
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

    private fun convertToDto(wikimediaPhoto: SongWikimediaPhoto): AdminWikimediaPhotoDto {
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
package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.SongApplication
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.*
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class SongController {

    private val log = LoggerFactory.getLogger(SongController::class.java)

    @Autowired
    private lateinit var songService: SongService

    @GetMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Summary::class)
    fun getSongs(): Flux<SongDto> {
        log.info("Requesting all songs...")
        return songService.findAllByStatusOrderByName(SongStatus.SHOW).map { convertToDto(it) }
    }

    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Detail::class)
    fun getSongById(@PathVariable("id") id: String): Mono<SongDto> {
        log.info("Requesting song with id {}", id)
        return songService.findById(id).map { convertToDto(it) }
    }

    private fun convertToDto(song: Song): SongDto {
        return SongDto(
                id = song.id,
                artist = song.artist,
                title = song.title,
                name = song.name,
                artistImage = song.artistImage,
                background = song.background,
                youtube = song.youtube,
                spotify = song.spotify,
                wikimediaPhotos = song.wikimediaPhotos.map { convertWikimediaPhotoToDto(it) }.toSet(),
                flickrPhotos = song.flickrPhotos.map {
                    PhotoDto(
                            id = it.id,
                            url = it.url,
                            farm = it.farm,
                            secret = it.secret,
                            server = it.server,
                            title = it.title,
                            owner = FlickrOwnerDto(
                                    id = it.ownerDetail?.id ?: "",
                                    username = it.ownerDetail?.username ?: "",
                                    photoUrl = it.ownerDetail?.photosUrl ?: ""
                            ),
                            license = FlickrLicenseDto(
                                    name = it.licenseDetail?.name ?: "",
                                    url = it.licenseDetail?.url ?: ""
                            )
                    )
                }.toSet(),
                sources = song.sources.map {
                    SourceDto(
                            url = it.url,
                            name = it.name
                    )
                }.toSet(),
                status = song.status?.name
        )
    }

    private fun convertWikimediaPhotoToDto(wikimediaPhoto: WikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }
}

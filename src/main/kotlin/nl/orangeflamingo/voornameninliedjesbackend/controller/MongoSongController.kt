package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.domain.MongoSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.*
import nl.orangeflamingo.voornameninliedjesbackend.service.MongoSongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/beta")
class MongoSongController {

    private val log = LoggerFactory.getLogger(MongoSongController::class.java)

    @Autowired
    private lateinit var mongoSongService: MongoSongService

    @GetMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Summary::class)
    fun getSongs(): List<SongDto> {
        log.info("Requesting all songs...")
        return mongoSongService.findAllByStatusOrderByName(SongStatus.SHOW).map { convertToDto(it) }
    }

    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Detail::class)
    fun getSongById(@PathVariable("id") id: String): SongDto {
        log.info("Requesting song with id {}", id)
        return convertToDto(mongoSongService.findById(id))
    }

    private fun convertToDto(song: MongoSong): SongDto {
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
                status = song.status?.name ?: SongStatus.IN_PROGRESS.name
        )
    }

    private fun convertWikimediaPhotoToDto(wikimediaPhoto: WikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }
}

package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import nl.orangeflamingo.voornameninliedjesbackend.domain.AggregateSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.PhotoDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.dto.*
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api")
class SongController {

    private val log = LoggerFactory.getLogger(SongController::class.java)

    private val songCache: LoadingCache<String, List<SongDto>> = CacheBuilder.newBuilder()
        .build(
            CacheLoader.from { _: String? -> songService.findAllByStatusOrderedByName(SongStatus.SHOW).map { convertToDto(it, emptyList()) } }
        )

    @Autowired
    private lateinit var songService: SongService

    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getSongById(@PathVariable id: Long): Mono<SongDto> {
        log.info("Requesting song with id $id...")
        val song = songService.findByIdDetails(id)
        return song.flickrPhotoDetail.collectList()
            .zipWith(song.wikipediaBackground.switchIfEmpty(Mono.just(song.background ?: "Geen achtergrond gevonden")))
            .map { it ->
                convertToDto(song, it.t1, it.t2)
            }
    }

    @GetMapping("/songs/{artist}/{title}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getSongByArtistAndTitle(@PathVariable artist: String, @PathVariable title: String): Mono<SongDto> {
        log.info("Requesting song with artist $artist and title $title...")
        val song = songService.findByArtistAndNameDetails(artist, title)
        return song.flickrPhotoDetail.collectList()
            .zipWith(song.wikipediaBackground.switchIfEmpty(Mono.just(song.background ?: "Geen achtergrond gevonden")))
            .map { it ->
                convertToDto(song, it.t1, it.t2)
            }
    }

    @GetMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Summary::class)
    fun getSongs(): List<SongDto> {
        log.info("Requesting all songs...")
        // the key is for show for now, cache dedicated for song list only
        songCache.refresh("songs")
        return songCache.getUnchecked("songs")
    }

    private fun convertToDto(song: AggregateSong, photos: List<PhotoDetail>, background: String = ""): SongDto {
        return SongDto(
            id = song.id.toString(),
            artist = song.artistName,
            title = song.title,
            name = song.name,
            artistImage = song.artistImage,
            background = background,
            youtube = song.youtube,
            spotify = song.spotify,
            wikimediaPhotos = song.wikimediaPhotos.map { convertWikimediaPhotoToDto(it) }.toSet(),
            flickrPhotos = photos.map {
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
            status = song.status.code
        )
    }

    private fun convertWikimediaPhotoToDto(wikimediaPhoto: ArtistWikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }
}
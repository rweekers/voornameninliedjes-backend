package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.dto.*
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api")
class SongController {

    private val log = LoggerFactory.getLogger(SongController::class.java)

    @Value("\${voornameninliedjes.cache.enabled}")
    private val useCache = false

    private val songsCache: LoadingCache<String, List<SongDto>> = CacheBuilder.newBuilder()
        .build(
            CacheLoader.from { _: String? ->
                songService.findAllByStatusOrderedByName(SongStatus.SHOW).map { convertToDto(it, emptyList()) }
            }
        )
    private val songCache: LoadingCache<Pair<String, String>, Mono<SongDto>> = CacheBuilder.newBuilder()
        .build(
            CacheLoader.from { key: Pair<String, String>? -> getSongDetails(key?.first ?: "", key?.second ?: "") }
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
        return if (useCache) {
            log.info("Requesting song with artist $artist and title $title from cache...")
            val artistTitlePair = Pair(artist, title)
            songCache.refresh(artistTitlePair)
            songCache.getIfPresent(artistTitlePair) ?: Mono.empty()
        } else {
            log.info("Requesting song with artist $artist and title $title...")
            getSongDetails(artist, title)
        }
    }

    private fun getSongDetails(artist: String, title: String): Mono<SongDto> {
        val song = songService.findByArtistAndNameDetails(artist, title)
        return song.flickrPhotoDetail.collectList()
            .zipWith(song.wikipediaBackground.switchIfEmpty(Mono.just(song.background ?: "Geen achtergrond gevonden")))
            .map {
                convertToDto(song, it.t1, it.t2)
            }
    }

    @GetMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Summary::class)
    fun getSongs(): List<SongDto> {
        return if (useCache) {
            log.info("Requesting all songs from cache...")
            // the key is for show for now, cache dedicated for song list only
            songsCache.refresh("songs")
            songsCache.getUnchecked("songs")
        } else {
            log.info("Requesting all songs...")
            songService.findAllByStatusOrderedByName(SongStatus.SHOW).map { convertToDto(it, emptyList()) }
        }
    }

    private fun convertToDto(song: AggregateSong, photos: List<PhotoDetail>, background: String = ""): SongDto {
        return SongDto(
            id = song.id.toString(),
            artist = song.artistName,
            title = song.title,
            name = song.name,
            artistImage = song.artistImage,
            background = background,
            wikipediaPage = song.wikipediaPage,
            youtube = song.youtube,
            spotify = song.spotify,
            wikimediaPhotos = mergeAndConvertWikimediaPhotos(song.artistWikimediaPhotos, song.songWikimediaPhotos),
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
            status = song.status.code,
            hasDetails = song.hasDetails
        )
    }

    private fun mergeAndConvertWikimediaPhotos(artistWikimediaPhoto: Set<ArtistWikimediaPhoto>, songWikimediaPhotos: Set<SongWikimediaPhoto>): Set<WikimediaPhotoDto> {
        val mergedPhotos = mutableSetOf<WikimediaPhotoDto>()
        mergedPhotos.addAll(songWikimediaPhotos.map { convertSongWikimediaPhotoToDto(it) })
        mergedPhotos.addAll(artistWikimediaPhoto.map { convertArtistWikimediaPhotoToDto(it) })
        return mergedPhotos
    }

    private fun convertArtistWikimediaPhotoToDto(wikimediaPhoto: ArtistWikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }

    private fun convertSongWikimediaPhotoToDto(wikimediaPhoto: SongWikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }
}
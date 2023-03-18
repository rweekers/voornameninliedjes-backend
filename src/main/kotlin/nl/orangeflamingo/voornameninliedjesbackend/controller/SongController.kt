package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.dto.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistNameStatisticsRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongNameStatisticsRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistNotFoundException
import nl.orangeflamingo.voornameninliedjesbackend.service.NotFoundException
import nl.orangeflamingo.voornameninliedjesbackend.service.SongNotFoundException
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*


@RestController
@RequestMapping("/api")
class SongController(
    private val songService: SongService,
    private val songNameStatisticsRepository: SongNameStatisticsRepository,
    private val artistNameStatisticsRepository: ArtistNameStatisticsRepository
) {

    private val log = LoggerFactory.getLogger(SongController::class.java)

    @Value("\${voornameninliedjes.cache.enabled}")
    private val useCache = false

    private val songCache: LoadingCache<Pair<String, String>, Mono<SongDto>> = CacheBuilder.newBuilder()
        .build(
            CacheLoader.from { key: Pair<String, String>? -> getSongDetails(key?.first ?: "", key?.second ?: "") }
        )

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(SongNotFoundException::class, ArtistNotFoundException::class)
    fun notFoundHandler(ex: NotFoundException) {
        log.warn("Gotten request with message {}", ex.message)
    }

    @GetMapping("/songs/{artist}/{title}")
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
            .map {
                convertToDto(song, it)
            }
    }

    @GetMapping(value = ["/songs/", "/songs"])
    @JsonView(Views.Summary::class)
    fun getSongs(@RequestParam("first-characters") firstCharacters: Optional<List<String>>): List<SongDto> {
        return firstCharacters.map { firstCharacter ->
            songService.findAllByStatusOrderedByNameFilteredByFirstCharacter(
                listOf(SongStatus.SHOW), firstCharacter.distinct().map { it.first() }
            )
        }.orElseGet { songService.findAllByStatusOrderedByName(SongStatus.SHOW) }
            .map { convertToDto(it, emptyList()) }
    }

    @GetMapping("/songs", params = ["first-character"])
    fun getSongsWithNameStartingWith(
        @RequestParam(name = "first-character") firstCharacter: String
    ): List<SongDto> {
        return songService.findByNameStartsWithAndStatusIn(firstCharacter, listOf(SongStatus.SHOW))
            .map { convertToDto(it, emptyList()) }
    }

    @GetMapping("/song-name-statistics")
    fun getSongNameStatistics(): List<SongNameStatistics> {
        return songNameStatisticsRepository.getCountPerName()
    }

    @GetMapping("/artist-name-statistics")
    fun getArtistNameStatistics(): List<ArtistNameStatistics> {
        return artistNameStatisticsRepository.getCountPerArtistname()
    }

    private fun convertToDto(song: AggregateSong, photos: List<PhotoDetail>): SongDto {
        return SongDto(
            artist = song.artistName,
            title = song.title,
            name = song.name,
            artistImage = song.artistImage,
            artistImageAttribution = song.artistImageAttribution,
            localImage = song.localImage,
            blurredImage = song.blurredImage,
            artistImageWidth = song.artistImageWidth,
            artistImageHeight = song.artistImageHeight,
            artistLastFmUrl = song.artistLastFmUrl,
            wikipediaPage = song.wikipediaPage,
            youtube = song.youtube,
            lastFmUrl = song.lastFmUrl,
            albumName = song.albumName,
            albumLastFmUrl = song.albumLastFmUrl,
            spotify = song.spotify,
            background = song.background,
            wikipediaNl = song.wikipediaContentNl,
            wikipediaEn = song.wikipediaContentEn,
            wikipediaSummaryEn = song.wikipediaSummaryEn,
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
            tags = song.tags.map {
                LastFmTagDto(
                    name = it.name,
                    url = it.url
                )
            }.toSet(),
            hasDetails = song.hasDetails
        )
    }

    private fun mergeAndConvertWikimediaPhotos(
        artistWikimediaPhoto: Set<ArtistWikimediaPhoto>,
        songWikimediaPhotos: Set<SongWikimediaPhoto>
    ): Set<WikimediaPhotoDto> {
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
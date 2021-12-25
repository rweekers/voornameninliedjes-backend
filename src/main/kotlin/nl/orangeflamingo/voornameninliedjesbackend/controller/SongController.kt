package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import nl.orangeflamingo.voornameninliedjesbackend.domain.AggregateSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistNameStatistics
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.PhotoDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongNameStatistics
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrLicenseDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrOwnerDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.PhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.SongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.SourceDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.WikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistNameStatisticsRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongNameStatisticsRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.Optional


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

    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getSongById(@PathVariable id: Long): Mono<SongDto> {
        log.info("Requesting song with id $id...")
        val song = songService.findByIdDetails(id)
        return song.flickrPhotoDetail.collectList()
            .zipWith(song.wikipediaBackground.switchIfEmpty(Mono.just(song.background ?: "Geen achtergrond gevonden")))
            .map { convertToDto(song, it.t1, it.t2) }
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
    fun getSongs(@RequestParam("first-characters") firstCharacters: Optional<List<String>>): List<SongDto> {
        return firstCharacters.map { firstCharacter ->
            songService.findAllByStatusOrderedByNameFilteredByFirstCharacter(
                listOf(SongStatus.SHOW, SongStatus.IN_PROGRESS), firstCharacter.distinct().map { it.first() }
            )
        }.orElseGet { songService.findAllByStatusOrderedByName(SongStatus.SHOW) }
            .map { convertToDto(it, emptyList()) }
    }

    @GetMapping("/song-name-statistics")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getSongNameStatistics(): List<SongNameStatistics> {
        return songNameStatisticsRepository.findAllStatusShowGroupedByNameOrderedByCountDescending()
    }

    @GetMapping("/artist-name-statistics")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getArtistNameStatistics(): List<ArtistNameStatistics> {
        return artistNameStatisticsRepository.findAllStatusShowGroupedByArtistNameOrderedByCountDescending()
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
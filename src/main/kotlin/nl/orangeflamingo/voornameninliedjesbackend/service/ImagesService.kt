package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.utils.clean
import nl.orangeflamingo.voornameninliedjesbackend.utils.removeDiacritics
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration


@Service
class ImagesService @Autowired constructor(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val imageClient: ImageClient
) {

    private val log = LoggerFactory.getLogger(ImagesService::class.java)

    private val maxDimensionBlur = 20

    @Value("\${voornameninliedjes.batch.interval}")
    private val interval: Long = 100

    fun downloadImages(overwrite: Boolean = false) {
        log.info("[image download] Starting downloading all images with interval millis $interval and overwrite: $overwrite")
        val songs = songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)

        Flux.fromIterable(songs)
            .delayElements(Duration.ofMillis(interval), Schedulers.boundedElastic())
            .subscribe({
                downloadImageForSong(it, overwrite)
            }, { log.error("[image download] Gotten error", it) }, { log.info("[image download] Done...") })
    }

    fun blurImages(overwrite: Boolean = false) {
        log.info("[image blur] Starting blurring all images with interval millis $interval and overwrite: $overwrite")
        val songs = songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)

        Flux.fromIterable(songs)
            .delayElements(Duration.ofMillis(interval), Schedulers.boundedElastic())
            .subscribe({
                blurImageForSong(it, overwrite)
            }, { log.error("[image blur] Gotten error", it) }, { log.info("[image blur] Done...") })
    }

    fun downloadImageForSong(song: Song, overwrite: Boolean = false) {
        val artist = artistRepository.findById(song.artist.id ?: throw IllegalStateException())
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artist.id} for song with title ${song.title} not found") }

        if (song.artistImage == null) {
            log.info("[image download] No artist image for ${artist.name} - ${song.title}")
            return
        }
        log.info("[image download] Downloading image ${song.artistImage} for ${artist.name} - ${song.title}")

        val extension = song.artistImage.substring(song.artistImage.lastIndexOf("."))
        val fileName =
            "${artist.name}_${song.title}$extension".removeDiacritics().replace(" ", "-").clean().lowercase()

        val encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        imageClient.downloadImage(song.artistImage, encodedFileName, overwrite)
            .publishOn(Schedulers.boundedElastic())
            .onErrorComplete {
                log.error("Could not download image ${song.artistImage} because of ${it.message}")
                true
            }
            .subscribe { _ ->
                songRepository.save(song.copy(localImage = fileName))
                log.info("[image download] Downloaded image for ${artist.name} - ${song.title} from ${song.artistImage} as $fileName")
            }
    }

    fun blurImageForSong(song: Song, overwrite: Boolean = false) {
        val artist = artistRepository.findById(song.artist.id ?: throw IllegalStateException())
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artist.id} for song with title ${song.title} not found") }

        if (song.artistImage == null) {
            log.info("[image blur] No artist image for ${artist.name} - ${song.title}")
            return
        }
        log.info("[image blur] Downloading image ${song.artistImage} for ${artist.name} - ${song.title}")
        if (overwrite || song.blurredImage == null) {
            imageClient.createImageBlur(song.artistImage, maxDimensionBlur, maxDimensionBlur)
                .publishOn(Schedulers.boundedElastic())
                .onErrorComplete {
                    log.error("[image blur] Could not create blur for ${artist.name} - ${song.title} because of ${it.message}")
                    true
                }
                .map { it.hash }
                .subscribe { encodedString ->
                    songRepository.save(song.copy(blurredImage = encodedString))
                    log.info("[image blur] Written blur string for ${artist.name} - ${song.title}")
                }
        } else {
            log.info("[image blur] Blur already known for ${artist.name} - ${song.title}")
        }
    }
}
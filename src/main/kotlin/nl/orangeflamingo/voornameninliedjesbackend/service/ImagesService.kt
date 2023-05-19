package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageApiClient
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
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import java.time.Duration


@Service
class ImagesService @Autowired constructor(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val fileService: FileService,
    private val imageApiClient: ImageApiClient
) {

    private val log = LoggerFactory.getLogger(ImagesService::class.java)

    private val maxDimensionBlur = 10

    @Value("\${voornameninliedjes.batch.interval}")
    private val interval: Long = 100

    @Value("\${voornameninliedjes.images.path}")
    private val imagesPath: String = "images"

    fun downloadImages(overwrite: Boolean = false) {
        log.info("[image download] Starting downloading all images with interval millis ${interval} and overwrite: $overwrite")
        val songs = songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)

        Flux.fromIterable(songs)
            .delayElements(Duration.ofMillis(interval), Schedulers.boundedElastic())
            .subscribe({
                downloadImageForSong(it, overwrite)
            }, { log.error("[image download] Gotten error", it) }, { log.info("[image download] Done...") })
    }

    fun blurImages(overwrite: Boolean = false) {
        log.info("[image blur] Starting blurring all images with interval millis ${interval} and overwrite: $overwrite")
        val songs = songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)

        Flux.fromIterable(songs)
            .delayElements(Duration.ofMillis(interval), Schedulers.boundedElastic())
            .subscribe({
                blurImageForSong(it, overwrite)
            }, { log.error("[image blur] Gotten error", it) }, { log.info("[image blur] Done...") })
    }

    fun downloadImageForSong(song: Song, overwrite: Boolean = false) {
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artists.first { it.originalArtist }} for song with title ${song.title} not found") }

        if (song.artistImage == null) {
            log.info("[image download] No artist image for ${artist.name} - ${song.title}")
            return
        }
        try {
            log.info("[image download] Downloading image ${song.artistImage} for ${artist.name} - ${song.title}")

            val extension = song.artistImage.substring(song.artistImage.lastIndexOf("."))
            val fileName =
                "${artist.name}_${song.title}$extension".removeDiacritics().replace(" ", "-").clean().lowercase()
            val localUrl = "$imagesPath/$fileName"
            if (overwrite || !fileService.fileExists(localUrl)) {
                fileService.writeToDisk(song.artistImage, localUrl)
                songRepository.save(song.copy(localImage = fileName))
                log.info("[image download] Written file for ${artist.name} - ${song.title}")
            } else {
                log.info("[image download] File already exists for ${artist.name} - ${song.title}")
            }
        } catch (e: IOException) {
            log.error(
                "[image download] Could not read or write image for ${artist.name} - ${song.title} due to error with message ${e.message}"
            )
        }
    }

    fun blurImageForSong(song: Song, overwrite: Boolean = false) {
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artists.first { it.originalArtist }} for song with title ${song.title} not found") }

        if (song.artistImage == null) {
            log.info("[image blur] No artist image for ${artist.name} - ${song.title}")
            return
        }
        try {
            log.info("[image blur] Downloading image ${song.artistImage} for ${artist.name} - ${song.title}")
            if (overwrite || song.blurredImage == null) {
                imageApiClient.createBlurString(song.artistImage, maxDimensionBlur, maxDimensionBlur)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe { encodedString -> songRepository.save(song.copy(blurredImage = encodedString))
                        log.info("[image blur] Written blur string for ${artist.name} - ${song.title}") }
            } else {
                log.info("[image blur] Blur already known for ${artist.name} - ${song.title}")
            }
        } catch (e: IOException) {
            log.error(
                // TODO check exception handling on Mono
                "[image blur] Could not read or write image for ${artist.name} - ${song.title} due to error with message ${e.message}"
            )
        }
    }

    private fun resizeImage(originalImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT)
        val outputImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        outputImage.graphics.drawImage(resultingImage, 0, 0, null)
        return outputImage
    }
}
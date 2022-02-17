package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.utils.Utils
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.Duration
import java.util.Base64
import javax.imageio.ImageIO
import kotlin.math.roundToInt


@Service
class ImagesService @Autowired constructor(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val fileService: FileService
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
                val imageUrl = Utils.resourceAsInputStream(song.artistImage)
                val inputS: InputStream = imageUrl.openStream()
                val input = ImageIO.read(inputS)

                // scale image to max 10x10
                val targetWidth = if (input.width >= input.height) maxDimensionBlur else ((input.width.toDouble() / input.height) * maxDimensionBlur).roundToInt()
                val targetHeight = if (input.height >= input.width) maxDimensionBlur else ((input.height.toDouble() / input.width) * maxDimensionBlur).roundToInt()

                val output = resizeImage(input, targetWidth, targetHeight)
                val baos = ByteArrayOutputStream()
                ImageIO.write(output, "png", baos)
                val bytes: ByteArray = baos.toByteArray()
                val encodedString: String = Base64.getEncoder().encodeToString(bytes)
                songRepository.save(song.copy(blurredImage = encodedString))
                log.info("[image blur] Written blur string for ${artist.name} - ${song.title}")
            } else {
                log.info("[image blur] Blur already known for ${artist.name} - ${song.title}")
            }
        } catch (e: IOException) {
            log.error(
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
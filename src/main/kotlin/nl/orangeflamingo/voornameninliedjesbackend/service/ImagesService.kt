package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.utils.Utils
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.File
import java.io.InputStream
import java.time.Duration


@Service
class ImagesService @Autowired constructor(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository
) {

    private val log = LoggerFactory.getLogger(ImagesService::class.java)

    fun downloadImages(overwrite: Boolean = false) {
        log.info("[image download] Starting downloading all images with overwrite: $overwrite")
        val songs = songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)

        Flux.fromIterable(songs)
            .delayElements(Duration.ofSeconds(3), Schedulers.boundedElastic())
            .subscribe({
                downloadImageForSong(it, overwrite)
            }, { log.error("[image download] Gotten error", it) }, { log.info("[image download] Done...") })
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

            val imageUrl = Utils.resourceAsInputStream(song.artistImage)
            val inputS: InputStream = imageUrl.openStream()
            val extension = song.artistImage.substring(song.artistImage.lastIndexOf("."))
            val fileName = "images/${artist.name}_${song.title}$extension".replace(" ", "-")
            val targetFile = File(fileName)
            if (overwrite || !targetFile.exists()) {
                FileUtils.copyInputStreamToFile(inputS, targetFile)
                songRepository.save(song.copy(localImage = fileName))
                log.info("[image download] Written file for ${artist.name} - ${song.title}")
            } else {
                log.info("[image download] File already exists for ${artist.name} - ${song.title}")
            }
        } catch (e: Exception) {
            log.error(
                "[image download] Could not read or write image for ${artist.name} - ${song.title} due to error",
                e
            )
        }
    }
}
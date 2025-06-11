package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration


@Service
class ImagesEnrichmentService @Autowired constructor(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val imageClient: ImageClient
) {

    private val log = LoggerFactory.getLogger(ImagesEnrichmentService::class.java)

    @Value("\${voornameninliedjes.batch.interval}")
    private val interval: Long = 100

    fun enrichImagesForSongs(updateAll: Boolean = false) {
        log.info("Starting images enrichment with update all: $updateAll")

        val songsToUpdate =
            if (updateAll) songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)
            else songRepository.findAllByStatusAndArtistImageIsNullOrArtistImageAttributionIsNull(
                SongStatus.SHOW.code
            )
        Flux.fromIterable(songsToUpdate)
            .delayElements(Duration.ofMillis(interval), Schedulers.boundedElastic())
            .subscribe({
                updateArtistImageForSong(it)
            }, { log.error("[image download] Gotten error", it) }, { log.info("[image download] Done...") })
    }

    fun updateArtistImageForSong(song: Song) {
        val artist = artistRepository.findById(song.artist.id ?: throw IllegalStateException())
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artist.id} for song with title ${song.title} not found") }
        try {

            log.info("[images] Updating ${song.title} from ${artist.name}")

            val urlToAttribution =
                if (song.photos.isNotEmpty()) song.photos.map { it.url to it.attribution }
                    .firstOrNull() else artist.photos.map { it.url to it.attribution }
                    .firstOrNull()
            if (urlToAttribution != null) {
                val (url, attribution) = urlToAttribution
                updateArtistImage(url.toString(), attribution, song)
            }
        } catch (e: Exception) {
            log.error("Could not update images information for ${artist.name} - ${song.title} due to error", e)
        }
    }

    private fun updateArtistImage(url: String, attribution: String, song: Song) {
        imageClient.getDimensions(url)
            .publishOn(Schedulers.boundedElastic())
            .onErrorComplete {
                val errorMessage =
                    "Could not find file on url $url for ${song.title} with error type ${it.javaClass.simpleName} and message ${it.message}"
                songRepository.save(song.copy(status = SongStatus.INCOMPLETE, remarks = errorMessage))
                log.error(errorMessage)
                true
            }
            .subscribe {
                log.info("Gotten width ${it.width} and height ${it.height} for $url")
                songRepository.save(
                    song.copy(
                        artistImage = url,
                        artistImageAttribution = attribution,
                        artistImageWidth = it.width,
                        artistImageHeight = it.height
                    )
                )
                log.info("Updated ${song.title} with attribution $attribution and url $url and width ${it.height} and height ${it.height}")
        }
    }

}
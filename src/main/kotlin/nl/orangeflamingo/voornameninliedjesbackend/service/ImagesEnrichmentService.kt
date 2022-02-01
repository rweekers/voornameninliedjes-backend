package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.utils.Utils
import org.apache.commons.imaging.Imaging
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.time.Duration


@Service
class ImagesEnrichmentService @Autowired constructor(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val flickrApiClient: FlickrApiClient
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
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artists.first { it.originalArtist }} for song with title ${song.title} not found") }
        try {

            log.info("[images] Updating ${song.title} from ${artist.name}")

            val urlToAttribution =
                if (song.wikimediaPhotos.isNotEmpty()) song.wikimediaPhotos.map { it.url to it.attribution }
                    .firstOrNull() else artist.wikimediaPhotos.map { it.url to it.attribution }
                    .firstOrNull()
            if (urlToAttribution != null) {
                val (url, attribution) = urlToAttribution
                updateArtistImage(url, attribution, song)
            } else {
                val photo = flickrApiClient.getPhoto(artist.flickrPhotos.first().flickrId)
                photo.subscribe { p ->
                    flickrApiClient.getOwnerInformation(p.ownerId).subscribe { o ->
                        val attribution = "Photo by ${o.username} to be found at ${p.url}"
                        updateArtistImage(p.url, attribution, song)
                    }
                }
            }
        } catch (e: Exception) {
            log.error("Could not update images information for ${artist.name} - ${song.title} due to error", e)
        }
    }

    private fun updateArtistImage(url: String, attribution: String, song: Song) {
        if (url != song.artistImage || attribution != song.artistImageAttribution || song.artistImageWidth == null || song.artistImageHeight == null) {
            try {
                val imageUrl = Utils.resourceAsInputStream(url)
                val inputS: InputStream = imageUrl.openStream()
                val imageInfo = Imaging.getImageInfo(inputS, imageUrl.file)
                log.info("Gotten width: ${imageInfo.width} and height: ${imageInfo.height}")

                songRepository.save(
                    song.copy(
                        artistImage = url,
                        artistImageAttribution = attribution,
                        artistImageWidth = imageInfo.width,
                        artistImageHeight = imageInfo.height
                    )
                )
                log.info("Updated ${song.title} with attribution $attribution and url $url and width ${imageInfo.width} and height ${imageInfo.height}")
            } catch (e: Exception) {
                when (e) {
                    is FileNotFoundException, is IOException -> {
                        val errorMessage =
                            "Could not find file on url $url for ${song.title} with error type ${e.javaClass.simpleName} and message ${e.message}"
                        songRepository.save(song.copy(status = SongStatus.INCOMPLETE, remarks = errorMessage))
                        log.error(errorMessage)
                    }
                    else -> throw e
                }
            }
        }
    }

}
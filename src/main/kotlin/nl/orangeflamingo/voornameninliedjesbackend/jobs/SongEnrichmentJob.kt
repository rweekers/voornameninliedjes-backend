package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.DbSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SongEnrichmentJob {

    private val log = LoggerFactory.getLogger(SongEnrichmentJob::class.java)

    @Autowired
    private lateinit var songRepository: SongRepository

    @Autowired
    private lateinit var flickrApiClient: FlickrApiClient

    @Scheduled(cron = "\${jobs.updateSong.cron}")
    fun updateSong() {
        log.info("Starting update job")
        songRepository.findAllByStatusOrderByName(SongStatus.SHOW)
                .switchIfEmpty { log.warn("This should not happen, no songs found with status SHOW") }
                .doOnComplete { log.info("Finished update job") }
                .subscribe { updateArtistImageForSong(it) }
    }

    @Scheduled(fixedRateString = "\${jobs.enrichSong.rate}")
    fun enrichSong() {
        log.info("Starting enrichment job")
        songRepository.findAllByStatusAndArtistImageIsNull(SongStatus.SHOW)
                .switchIfEmpty { log.info("No songs with flickr photos without artist image, finished enrichment job") }
                .doOnComplete { log.info("Finished enrichment job") }
                .subscribe { updateArtistImageForSong(it) }
    }

    private fun updateArtistImageForSong(song: DbSong) {
        val url = song.wikimediaPhotos.map { wikimediaPhoto -> wikimediaPhoto.url }.firstOrNull()
        if (url != null) {
            updateArtistImage(url, song)
        } else {
            val photo = flickrApiClient.getPhoto(song.flickrPhotos.first())
            photo.subscribe { p ->
                updateArtistImage(p.url, song)
            }
        }
    }

    private fun updateArtistImage(url: String, song: DbSong) {
        if (url != song.artistImage) {
            val updatedSong = song.copy(artistImage = url)
            songRepository.save(updatedSong)
                    .subscribe { s -> log.info("Updated song $s with url ${s.artistImage}") }
        }
    }

}
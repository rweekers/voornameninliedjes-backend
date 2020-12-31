package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.DbSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoSongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SongEnrichmentJob {

    private val log = LoggerFactory.getLogger(SongEnrichmentJob::class.java)

    @Autowired
    private lateinit var mongoSongRepository: MongoSongRepository

    @Autowired
    private lateinit var flickrApiClient: FlickrApiClient

    @Scheduled(cron = "\${jobs.updateSong.cron}")
    fun updateSong() {
        log.info("Starting update job")
        mongoSongRepository.findAllByStatusOrderByName(SongStatus.SHOW)
            .forEach { updateArtistImageForSong(it) }
    }

    @Scheduled(fixedRateString = "\${jobs.enrichSong.rate}")
    fun enrichSong() {
        log.info("Starting enrichment job")
        mongoSongRepository.findAllByStatusAndArtistImageIsNull(SongStatus.SHOW)
            .forEach { updateArtistImageForSong(it) }
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
            mongoSongRepository.save(updatedSong)
        }
    }

}
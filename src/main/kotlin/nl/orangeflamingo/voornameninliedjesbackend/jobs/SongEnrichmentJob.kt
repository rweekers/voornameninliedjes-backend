package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "jobs", name = ["enabled"], havingValue = "true")
class SongEnrichmentJob {

    private val log = LoggerFactory.getLogger(SongEnrichmentJob::class.java)

    @Autowired
    private lateinit var songRepository: SongRepository

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var flickrApiClient: FlickrApiClient

    @Scheduled(cron = "\${jobs.updateSong.cron}")
    fun updateSong() {
        log.info("Starting update job")
        songRepository.findAllByStatusOrderedByName(SongStatus.SHOW)
            .forEach { updateArtistImageForSong(it) }
    }

    @Scheduled(fixedRateString = "\${jobs.enrichSong.rate}")
    fun enrichSong() {
        log.info("Starting enrichment job")
        songRepository.findAllByStatusAndArtistImageIsNull(SongStatus.SHOW)
            .forEach { updateArtistImageForSong(it) }
    }

    private fun updateArtistImageForSong(song: Song) {
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()
        val url = artist.wikimediaPhotos.map { wikimediaPhoto -> wikimediaPhoto.url }.firstOrNull()
        if (url != null) {
            updateArtistImage(url, song)
        } else {
            val photo = flickrApiClient.getPhoto(artist.flickrPhotos.first().flickrId)
            photo.subscribe { p ->
                updateArtistImage(p.url, song)
            }
        }
    }

    private fun updateArtistImage(url: String, song: Song) {
        if (url != song.artistImage) {
            val updatedSong = song.copy(artistImage = url)
            songRepository.save(updatedSong)
        }
    }

}
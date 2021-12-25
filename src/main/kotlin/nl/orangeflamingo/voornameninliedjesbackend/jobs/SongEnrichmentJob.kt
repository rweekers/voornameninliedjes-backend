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
class SongEnrichmentJob(
    @Autowired val songRepository: SongRepository,
    @Autowired val artistRepository: ArtistRepository,
    @Autowired val flickrApiClient: FlickrApiClient
) {

    private val log = LoggerFactory.getLogger(SongEnrichmentJob::class.java)

    @Scheduled(cron = "\${jobs.updateSong.cron}")
    fun updateSong() {
        log.info("Starting update job")
        songRepository.findAllByStatusOrderedByName(SongStatus.SHOW.code)
            .forEach { updateArtistImageForSong(it) }
    }

    @Scheduled(fixedRateString = "\${jobs.enrichSong.rate}")
    fun enrichSong() {
        log.info("Starting enrichment job")
        songRepository.findAllByStatusAndArtistImageIsNull(SongStatus.SHOW.code)
            .forEach { updateArtistImageForSong(it) }
    }

    private fun updateArtistImageForSong(song: Song) {
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()
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
    }

    private fun updateArtistImage(url: String, attribution: String, song: Song) {
        if (url != song.artistImage) {
            val updatedSong = song.copy(artistImage = url, artistImageAttribution = attribution)
            songRepository.save(updatedSong)
        }
    }

}
package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.service.ImagesEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.WikipediaEnrichmentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SongEnrichmentJob @Autowired constructor(
    private val imagesEnrichmentService: ImagesEnrichmentService,
    private val wikipediaEnrichmentService: WikipediaEnrichmentService,
    private val lastFmEnrichmentService: LastFmEnrichmentService
) {

    private val log = LoggerFactory.getLogger(SongEnrichmentJob::class.java)

    @Value("\${jobs.enrichSong.enabled:true}")
    private var enrichSongsEnabled: Boolean = true

    @Scheduled(cron = "\${jobs.updateSong.cron}")
    fun updateSongImages() {
        log.info("Starting update job")
        imagesEnrichmentService.enrichImagesForSongs(true)
        wikipediaEnrichmentService.enrichWikipediaForSongs(true)
        lastFmEnrichmentService.enrichLastFmInfoForSongs(true)
    }

    @Scheduled(fixedRateString = "\${jobs.enrichSong.rate}")
    fun enrichSongImages() {
        if (!enrichSongsEnabled) {
            return;
        }
        log.info("Starting enrichment job")
        imagesEnrichmentService.enrichImagesForSongs()
        wikipediaEnrichmentService.enrichWikipediaForSongs()
        lastFmEnrichmentService.enrichLastFmInfoForSongs()
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.service.ImagesEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.WikipediaEnrichmentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "jobs", name = ["enabled"], havingValue = "true")
class SongEnrichmentJob @Autowired constructor(
    private val imagesEnrichmentService: ImagesEnrichmentService,
    private val wikipediaEnrichmentService: WikipediaEnrichmentService,
    private val lastFmEnrichmentService: LastFmEnrichmentService
) {

    private val log = LoggerFactory.getLogger(SongEnrichmentJob::class.java)

    @Scheduled(cron = "\${jobs.updateSong.cron}")
    fun updateSongImages() {
        log.info("Starting update job")
        imagesEnrichmentService.enrichImagesForSongs(true)
        wikipediaEnrichmentService.enrichWikipediaForSongs(true)
        lastFmEnrichmentService.enrichLastFmInfoForSongs(true)
    }

    @Scheduled(fixedRateString = "\${jobs.enrichSong.rate}")
    fun enrichSongImages() {
        log.info("Starting enrichment job")
        imagesEnrichmentService.enrichImagesForSongs()
        wikipediaEnrichmentService.enrichWikipediaForSongs()
        lastFmEnrichmentService.enrichLastFmInfoForSongs()
    }
}
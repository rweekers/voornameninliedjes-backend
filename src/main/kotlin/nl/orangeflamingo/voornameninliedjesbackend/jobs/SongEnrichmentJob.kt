package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.service.SongEnrichmentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "jobs", name = ["enabled"], havingValue = "true")
class SongEnrichmentJob(
    @Autowired val songEnrichmentService: SongEnrichmentService
) {

    private val log = LoggerFactory.getLogger(SongEnrichmentJob::class.java)

    @Scheduled(cron = "\${jobs.updateSong.cron}")
    fun updateSong() {
        log.info("Starting update job")
        songEnrichmentService.enrichSongs(true)
    }

    @Scheduled(fixedRateString = "\${jobs.enrichSong.rate}")
    fun enrichSong() {
        log.info("Starting enrichment job")
        songEnrichmentService.enrichSongs()
    }
}
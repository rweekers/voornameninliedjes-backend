package nl.orangeflamingo.voornameninliedjesbackend.jobs

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.WikipediaEnrichmentService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SongEnrichmentJobTest {

    private val mockWikipediaEnrichmentService = mockk<WikipediaEnrichmentService>()
    private val mockLastFmEnrichmentService = mockk<LastFmEnrichmentService>()
    private val songEnrichmentJob = SongEnrichmentJob(
        mockWikipediaEnrichmentService,
        mockLastFmEnrichmentService
    )

    @BeforeEach
    fun init() {
        every { mockWikipediaEnrichmentService.enrichWikipediaForSongs(any()) } just Runs
        every { mockLastFmEnrichmentService.enrichLastFmInfoForSongs(any()) } just Runs

    }

    @Test
    fun `test updateSong`() {
        songEnrichmentJob.updateSongImages()
        verify { mockWikipediaEnrichmentService.enrichWikipediaForSongs(true) }
        verify { mockLastFmEnrichmentService.enrichLastFmInfoForSongs(true) }
    }

    @Test
    fun `test enrichSong`() {
        songEnrichmentJob.enrichSongImages()
        verify { mockWikipediaEnrichmentService.enrichWikipediaForSongs() }
        verify { mockLastFmEnrichmentService.enrichLastFmInfoForSongs() }
    }

}
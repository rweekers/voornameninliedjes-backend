package nl.orangeflamingo.voornameninliedjesbackend.jobs

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import nl.orangeflamingo.voornameninliedjesbackend.service.ImagesEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.WikipediaEnrichmentService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SongEnrichmentJobTest {

    private val mockImagesEnrichmentService= mockk<ImagesEnrichmentService>()
    private val mockWikipediaEnrichmentService = mockk<WikipediaEnrichmentService>()
    private val mockLastFmEnrichmentService = mockk<LastFmEnrichmentService>()
    private val songEnrichmentJob = SongEnrichmentJob(
        mockImagesEnrichmentService,
        mockWikipediaEnrichmentService,
        mockLastFmEnrichmentService
    )

    @BeforeEach
    fun init() {
        every { mockImagesEnrichmentService.enrichImagesForSongs(any()) } just Runs
        every { mockWikipediaEnrichmentService.enrichWikipediaForSongs(any()) } just Runs
        every { mockLastFmEnrichmentService.enrichLastFmInfoForSongs(any()) } just Runs

    }

    @Test
    fun `test updateSong`() {
        songEnrichmentJob.updateSongImages()
        verify { mockImagesEnrichmentService.enrichImagesForSongs(true) }
        verify { mockWikipediaEnrichmentService.enrichWikipediaForSongs(true) }
        verify { mockLastFmEnrichmentService.enrichLastFmInfoForSongs(true) }
    }

    @Test
    fun `test enrichSong`() {
        songEnrichmentJob.enrichSongImages()
        verify { mockImagesEnrichmentService.enrichImagesForSongs() }
        verify { mockWikipediaEnrichmentService.enrichWikipediaForSongs() }
        verify { mockLastFmEnrichmentService.enrichLastFmInfoForSongs() }
    }

}
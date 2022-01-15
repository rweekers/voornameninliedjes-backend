package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.service.ImagesEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.WikipediaEnrichmentService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SongEnrichmentJobTest {

    private val mockImagesEnrichmentService = mock(ImagesEnrichmentService::class.java)
    private val mockWikipediaEnrichmentService = mock(WikipediaEnrichmentService::class.java)
    private val mockLastFmEnrichmentService = mock(LastFmEnrichmentService::class.java)
    private val songEnrichmentJob = SongEnrichmentJob(
        mockImagesEnrichmentService,
        mockWikipediaEnrichmentService,
        mockLastFmEnrichmentService
    )

    @Test
    fun `test updateSong`() {
        songEnrichmentJob.updateSongImages()
        verify(mockImagesEnrichmentService).enrichImagesForSongs(true)
        verify(mockWikipediaEnrichmentService).enrichWikipediaForSongs(true)
        verify(mockLastFmEnrichmentService).enrichLastFmInfoForSongs(true)
    }

    @Test
    fun `test enrichSong`() {
        songEnrichmentJob.enrichSongImages()
        verify(mockImagesEnrichmentService).enrichImagesForSongs()
        verify(mockWikipediaEnrichmentService).enrichWikipediaForSongs()
        verify(mockLastFmEnrichmentService).enrichLastFmInfoForSongs()
    }

}
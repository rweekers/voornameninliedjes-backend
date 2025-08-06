package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmEnrichmentService
import nl.orangeflamingo.voornameninliedjesbackend.service.WikipediaEnrichmentService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SongEnrichmentJobTest {

    private val mockWikipediaEnrichmentService = mock(WikipediaEnrichmentService::class.java)
    private val mockLastFmEnrichmentService = mock(LastFmEnrichmentService::class.java)
    private val songEnrichmentJob = SongEnrichmentJob(
        mockWikipediaEnrichmentService,
        mockLastFmEnrichmentService
    )

    @Test
    fun `test updateSong`() {
        songEnrichmentJob.updateSongImages()
        verify(mockWikipediaEnrichmentService).enrichWikipediaForSongs(true)
        verify(mockLastFmEnrichmentService).enrichLastFmInfoForSongs(true)
    }

    @Test
    fun `test enrichSong`() {
        songEnrichmentJob.enrichSongImages()
        verify(mockWikipediaEnrichmentService).enrichWikipediaForSongs()
        verify(mockLastFmEnrichmentService).enrichLastFmInfoForSongs()
    }

}
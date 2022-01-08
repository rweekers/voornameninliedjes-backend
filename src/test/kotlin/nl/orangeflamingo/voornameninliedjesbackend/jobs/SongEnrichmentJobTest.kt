package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.service.ImagesEnrichmentService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SongEnrichmentJobTest {

    private val mockImagesEnrichmentService = mock(ImagesEnrichmentService::class.java)
    private val songEnrichmentJob = SongEnrichmentJob(
        mockImagesEnrichmentService
    )

    @Test
    fun `test updateSong`() {
        songEnrichmentJob.updateSongImages()
        verify(mockImagesEnrichmentService).enrichImagesForSongs(true)
    }

    @Test
    fun `test enrichSong`() {
        songEnrichmentJob.enrichSongImages()
        verify(mockImagesEnrichmentService).enrichImagesForSongs()
    }

}
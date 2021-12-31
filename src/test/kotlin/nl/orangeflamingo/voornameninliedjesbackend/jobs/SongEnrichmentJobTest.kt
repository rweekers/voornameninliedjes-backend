package nl.orangeflamingo.voornameninliedjesbackend.jobs

import nl.orangeflamingo.voornameninliedjesbackend.service.SongEnrichmentService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SongEnrichmentJobTest {

    private val mockSongEnrichmentService = mock(SongEnrichmentService::class.java)
    private val songEnrichmentJob = SongEnrichmentJob(
        mockSongEnrichmentService
    )

    @Test
    fun `test updateSong`() {
        songEnrichmentJob.updateSong()
        verify(mockSongEnrichmentService).enrichSongs(true)
    }

    @Test
    fun `test enrichSong`() {
        songEnrichmentJob.enrichSong()
        verify(mockSongEnrichmentService).enrichSongs()
    }

}
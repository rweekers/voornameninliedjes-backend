package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrHttpApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatusStatistics
import nl.orangeflamingo.voornameninliedjesbackend.domain.TestSong
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional

class SongServiceTest {

    private val artistRepository = mock(ArtistRepository::class.java)

    private val songRepository = mock(SongRepository::class.java)

    private val flickrApiClient = mock(FlickrHttpApiClient::class.java)

    private val songService: SongService = SongService(
        artistRepository, songRepository, flickrApiClient
    )

    @Test
    fun countSongsTest() {
        `when`(songRepository.count()).thenReturn(1)
        assertEquals(1, songService.countSongs())
    }

    @Test
    fun songStatisticsTest() {
        `when`(songRepository.getCountPerStatus()).thenReturn(listOf(
            SongStatusStatistics(SongStatus.SHOW, 8),
            SongStatusStatistics(SongStatus.IN_PROGRESS, 3),
            SongStatusStatistics(SongStatus.INCOMPLETE, 1),
            SongStatusStatistics(SongStatus.TO_BE_DELETED, 2)
        ))
        assertEquals(4, songService.countSongsByStatus().size)
    }

    @Test
    fun findByIdWithArtistNotFound() {
        val returnSong = TestSong(
            artist = 1
        )
        `when`(songRepository.findById(1)).thenReturn(Optional.of(returnSong.toDomain()))
        `when`(artistRepository.findById(1)).thenReturn(Optional.empty())

        assertThatThrownBy { songService.findById(1) }
            .isInstanceOf(ArtistNotFoundException::class.java)
            .hasMessage("Artist with artist id 1 for title Lucy in the Sky with Diamonds not found")
    }
}
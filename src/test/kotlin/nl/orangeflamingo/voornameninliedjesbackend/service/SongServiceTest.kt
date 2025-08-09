package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.every
import io.mockk.mockk
import java.util.Optional
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatusStatistics
import nl.orangeflamingo.voornameninliedjesbackend.domain.TestSong
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class SongServiceTest {

    private val artistRepository = mockk<ArtistRepository>()

    private val songRepository = mockk<SongRepository>()

    private val songService: SongService = SongService(
        artistRepository, songRepository
    )

    @Test
    fun countSongsTest() {
        every { songRepository.count() } returns 1
        assertThat(songService.countSongs()).isEqualTo(1)
    }

    @Test
    fun songStatisticsTest() {
        every { songRepository.getCountPerStatus() } returns listOf(
            SongStatusStatistics(SongStatus.SHOW, 8),
            SongStatusStatistics(SongStatus.IN_PROGRESS, 3),
            SongStatusStatistics(SongStatus.INCOMPLETE, 1),
            SongStatusStatistics(SongStatus.TO_BE_DELETED, 2)
        )
        assertThat(songService.countSongsByStatus()).hasSize(4)
    }

    @Test
    fun findByIdWithArtistNotFound() {
        val returnSong = TestSong(
            artist = 1
        )
        every { songRepository.findById(1) } returns Optional.of(returnSong.toDomain())
        every { artistRepository.findById(1) } returns Optional.empty()

        assertThatThrownBy { songService.findById(1) }
            .isInstanceOf(ArtistNotFoundException::class.java)
            .hasMessage("Artist with artist id 1 for title Lucy in the Sky with Diamonds not found")
    }
}
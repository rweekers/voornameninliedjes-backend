package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWithArtist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepositoryV2
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Pageable

class SongServiceV2Test {

    private val songRepositoryV2 = mock(SongRepositoryV2::class.java)

    private val songServiceV2: SongServiceV2 = SongServiceV2(
        songRepositoryV2
    )

    @Test
    fun `get all songs`() {
        val song = mock(SongWithArtist::class.java)
        val status = SongStatus.SHOW
        val name = "Al"
        val limit = 10
        val page = 3
        whenever(songRepositoryV2.findAllSongsWithArtistsStartingWith(status.code, name, limit, page))
            .thenReturn(listOf(song))
        val songs = songServiceV2.findByNameStartingWith(name, status, Pageable.ofSize(limit).withPage(page))
        assertThat(songs).isEqualTo(listOf(song))
        verify(songRepositoryV2).findAllSongsWithArtistsStartingWith(status = "SHOW", name = "Al", limit = 10, offset = page)
    }
}
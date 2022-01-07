package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrHttpApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaHttpApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistRef
import nl.orangeflamingo.voornameninliedjesbackend.domain.TestSong
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.Optional

class SongServiceTest {

    private val artistRepository = mock(ArtistRepository::class.java)

    private val songRepository = mock(SongRepository::class.java)

    private val flickrApiClient = mock(FlickrHttpApiClient::class.java)

    private val wikipediaHttpApiClient = mock(WikipediaHttpApiClient::class.java)

    private val songService: SongService = SongService(
        artistRepository, songRepository, flickrApiClient, wikipediaHttpApiClient
    )

    @Test
    fun countSongsTest() {
        `when`(songRepository.count()).thenReturn(1)
        assertEquals(1, songService.countSongs())
    }

    @Test
    fun findByIdWithArtistNotFound() {
        val returnSong = TestSong(
            artists = mutableSetOf(
                ArtistRef(1, true)
            )
        )
        `when`(songRepository.findById(1)).thenReturn(Optional.of(returnSong.toDomain()))
        `when`(artistRepository.findById(1)).thenReturn(Optional.empty())

        assertThatThrownBy { songService.findById(1) }
            .isInstanceOf(ArtistNotFoundException::class.java)
            .hasMessage("Artist with artistRef ArtistRef(artist=1, originalArtist=true) for title Lucy in the Sky with Diamonds not found")
    }
}
package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrHttpApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaHttpApiClient
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

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
}
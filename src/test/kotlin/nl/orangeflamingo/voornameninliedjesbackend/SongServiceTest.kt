package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrHttpApiClient
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class SongServiceTest {

    private val artistRepository = Mockito.mock(ArtistRepository::class.java)

    private val songRepository = Mockito.mock(SongRepository::class.java)

    private val flickrApiClient = Mockito.mock(FlickrHttpApiClient::class.java)

    private val songService: SongService = SongService(
        artistRepository, songRepository, flickrApiClient
    )

    @Test
    fun countSongsTest() {
        `when`(songRepository.count()).thenReturn(1)
        assertEquals(1, songService.countSongs())
    }
}
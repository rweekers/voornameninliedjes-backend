package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoSongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class SongServiceTest() {

    private val mongoSongRepository = Mockito.mock(MongoSongRepository::class.java)

    private val artistRepository = Mockito.mock(ArtistRepository::class.java)

    private val songRepository = Mockito.mock(SongRepository::class.java)

    private val flickrApiClient = Mockito.mock(FlickrApiClient::class.java)

    private val songService: SongService = SongService(
        artistRepository, mongoSongRepository, songRepository, flickrApiClient
    )

    @Test
    fun countSongsTest() {
        `when`(songRepository.count()).thenReturn(1)
        Assertions.assertEquals(1, songService.countSongs())
    }
}
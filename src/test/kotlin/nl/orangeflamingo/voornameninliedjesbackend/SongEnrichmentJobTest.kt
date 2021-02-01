package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.jobs.SongEnrichmentJob
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import reactor.core.publisher.Mono
import java.util.*

class SongEnrichmentJobTest {

    private val mockSongRepository = mock(SongRepository::class.java)
    private val mockArtistRepository = mock(ArtistRepository::class.java)
    private val mockFlickrApiClient = mock(FlickrApiClient::class.java)
    private val songEnrichmentJob = SongEnrichmentJob(
        mockSongRepository,
        mockArtistRepository,
        mockFlickrApiClient
    )
    val song = Song(
        id = 1,
        title = "Michelle",
        name = "Michelle",
        artists = mutableSetOf(ArtistRef(100)),
        status = SongStatus.SHOW
    )

    @BeforeEach
    fun init() {
        `when`(mockSongRepository.findAllByStatusOrderedByName(SongStatus.SHOW)).thenReturn(
            listOf(song)
        )
        `when`(mockSongRepository.findAllByStatusAndArtistImageIsNull(SongStatus.SHOW)).thenReturn(
            listOf(song)
        )
        `when`(mockArtistRepository.findById(100)).thenReturn(
            Optional.of(
                Artist(
                    id = 100,
                    name = "The Beatles",
                    flickrPhotos = mutableSetOf(ArtistFlickrPhoto("1000"))
                )
            )
        )
        `when`(mockFlickrApiClient.getPhoto("1000")).thenReturn(
            Mono.just(
                FlickrPhotoDetail(
                    url = "https://flickrUrl",
                    title = "flickrTitle",
                    farm = "flickrFarm",
                    server = "flickrServer",
                    id = "1000",
                    secret = "flickrSecret",
                    ownerId = "flickrOwnerId",
                    licenseId = "flickrLicenseId"
                )
            )
        )
    }

    @Test
    fun `test updateSong`() {
        songEnrichmentJob.updateSong()
        verify(mockSongRepository).save(
            song.copy(artistImage = "https://flickrUrl")
        )
    }

    @Test
    fun `test enrichSong`() {
        songEnrichmentJob.enrichSong()
        verify(mockSongRepository).save(
            song.copy(artistImage = "https://flickrUrl")
        )
    }

}
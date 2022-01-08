package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistRef
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiOwner
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrPhotoDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import reactor.core.publisher.Mono
import java.util.Optional

class ImagesEnrichmentServiceTest {

    private val mockSongRepository = mock(SongRepository::class.java)
    private val mockArtistRepository = mock(ArtistRepository::class.java)
    private val mockFlickrApiClient = mock(FlickrApiClient::class.java)
    private val imagesEnrichmentService = ImagesEnrichmentService(
        mockSongRepository,
        mockArtistRepository,
        mockFlickrApiClient
    )
    private val song = Song(
        id = 1,
        title = "Michelle",
        name = "Michelle",
        artists = mutableSetOf(ArtistRef(100)),
        status = SongStatus.SHOW
    )

    @BeforeEach
    fun init() {
        `when`(mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)).thenReturn(
            listOf(song)
        )
        `when`(mockSongRepository.findAllByStatusAndArtistImageIsNullOrArtistImageAttributionIsNull(SongStatus.SHOW.code)).thenReturn(
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
        `when`(mockFlickrApiClient.getOwnerInformation("flickrOwnerId")).thenReturn(
            Mono.just(
                FlickrApiOwner(
                    id = "flickrOwnerId",
                    username = "Some flickr owner",
                    photosUrl = "https://flickrUrl"
                )
            )
        )
    }

    @Test
    fun `test updateSong`() {
        imagesEnrichmentService.enrichImagesForSongs(true)
        verify(mockSongRepository).findAllByStatusOrderedByNameAndTitle("SHOW")
        verify(mockSongRepository).save(
            song.copy(artistImage = "https://flickrUrl", artistImageAttribution = "Photo by Some flickr owner to be found at https://flickrUrl")
        )
    }

    @Test
    fun `test enrichSong`() {
        imagesEnrichmentService.enrichImagesForSongs()
        verify(mockSongRepository).save(
            song.copy(artistImage = "https://flickrUrl", artistImageAttribution = "Photo by Some flickr owner to be found at https://flickrUrl")
        )
    }

}
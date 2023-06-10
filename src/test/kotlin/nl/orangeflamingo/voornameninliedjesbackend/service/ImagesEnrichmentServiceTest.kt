package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.ImageApiClient
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
import org.mockito.Mockito.after
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import java.util.Optional

class ImagesEnrichmentServiceTest {

    private val mockSongRepository = mock(SongRepository::class.java)
    private val mockArtistRepository = mock(ArtistRepository::class.java)
    private val mockFlickrApiClient = mock(FlickrApiClient::class.java)
    private val mockImageApiClient = mock(ImageApiClient::class.java)
    private val imagesEnrichmentService = ImagesEnrichmentService(
        mockSongRepository,
        mockArtistRepository,
        mockFlickrApiClient,
        mockImageApiClient
    )
    private val song = Song(
        id = 1,
        title = "Michelle",
        name = "Michelle",
        artists = mutableSetOf(ArtistRef(100)),
        status = SongStatus.SHOW
    )

    private val artist = Artist(
        id = 100,
        name = "The Beatles",
        flickrPhotos = mutableSetOf(ArtistFlickrPhoto("1000"))
    )

    private val flickrPhotoDetail = FlickrPhotoDetail(
        url = "classpath:images/test.png",
        title = "flickrTitle",
        farm = "flickrFarm",
        server = "flickrServer",
        id = "1000",
        secret = "flickrSecret",
        ownerId = "flickrOwnerId",
        licenseId = "flickrLicenseId"
    )

    private val flickrApiOwner = FlickrApiOwner(
        id = "flickrOwnerId",
        username = "Some flickr owner",
        photosUrl = "classpath:images/test.png"
    )

    @BeforeEach
    fun init() {
        whenever(mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)).thenReturn(
            listOf(song)
        )
        whenever(mockSongRepository.findAllByStatusAndArtistImageIsNullOrArtistImageAttributionIsNull(SongStatus.SHOW.code)).thenReturn(
            listOf(song)
        )
        whenever(mockArtistRepository.findById(100)).thenReturn(Optional.of(artist))
        whenever(mockFlickrApiClient.getPhoto("1000")).thenReturn(Mono.just(flickrPhotoDetail))
        whenever(mockFlickrApiClient.getOwnerInformation("flickrOwnerId")).thenReturn(Mono.just(flickrApiOwner))
        whenever(mockImageApiClient.getDimensions(any())).thenReturn(Mono.just(Pair(234, 234)))
    }

    @Test
    fun `test updateSong`() {
        imagesEnrichmentService.enrichImagesForSongs(true)
        verify(mockSongRepository).findAllByStatusOrderedByNameAndTitle("SHOW")
        verify(mockSongRepository, after(240)).save(
            song.copy(
                artistImage = "classpath:images/test.png",
                artistImageWidth = 234,
                artistImageHeight = 234,
                artistImageAttribution = "Photo by Some flickr owner to be found at classpath:images/test.png"
            )
        )
    }

    @Test
    fun `test enrichSong`() {
        imagesEnrichmentService.enrichImagesForSongs()
        verify(mockSongRepository, after(240)).save(
            song.copy(
                artistImage = "classpath:images/test.png",
                artistImageWidth = 234,
                artistImageHeight = 234,
                artistImageAttribution = "Photo by Some flickr owner to be found at classpath:images/test.png"
            )
        )
    }

    @Test
    fun `test image not found `() {
        whenever(mockFlickrApiClient.getPhoto("1000")).thenReturn(Mono.just(flickrPhotoDetail.copy(url = "classpath:images/notfound.png")))
        whenever(mockImageApiClient.getDimensions(any())).thenReturn(Mono.error(RuntimeException("Not found")))
        imagesEnrichmentService.enrichImagesForSongs()
        verify(mockSongRepository, after(120)).save(
            song.copy(
                status = SongStatus.INCOMPLETE,
                remarks = "Could not find file on url classpath:images/notfound.png for Michelle with error type RuntimeException and message Not found"
            )
        )
    }

}
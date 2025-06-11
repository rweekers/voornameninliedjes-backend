package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.after
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout
import org.mockito.kotlin.whenever
import org.springframework.data.jdbc.core.mapping.AggregateReference
import reactor.core.publisher.Mono
import java.util.Optional

class ImagesEnrichmentServiceTest {

    private val mockSongRepository = mock(SongRepository::class.java)
    private val mockArtistRepository = mock(ArtistRepository::class.java)
    private val mockImageClient = mock(ImageClient::class.java)
    private val imagesEnrichmentService = ImagesEnrichmentService(
        mockSongRepository,
        mockArtistRepository,
        mockImageClient
    )
    private val song = Song(
        id = 1,
        title = "Michelle",
        name = "Michelle",
        artist = AggregateReference.to(100),
        status = SongStatus.SHOW
    )

    private val artist = Artist(
        id = 100,
        name = "The Beatles",
        photos = mutableSetOf(ArtistPhoto(url = "https://somephoto.com", attribution = "Some attribution"))
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
        whenever(mockImageClient.getDimensions(any())).thenReturn(Mono.just(ImageDimensionsDto("imageName", 234, 234)))
    }

    @Test
    fun `test updateSong`() {
        imagesEnrichmentService.enrichImagesForSongs(true)
        verify(mockSongRepository).findAllByStatusOrderedByNameAndTitle("SHOW")
        verify(mockSongRepository, timeout(1000)).save(
            song.copy(
                artistImage = "https://somephoto.com",
                artistImageWidth = 234,
                artistImageHeight = 234,
                artistImageAttribution = "Some attribution"
            )
        )
    }

    @Test
    fun `test enrichSong`() {
        imagesEnrichmentService.enrichImagesForSongs()
        verify(mockSongRepository, after(500)).save(
            song.copy(
                artistImage = "https://somephoto.com",
                artistImageWidth = 234,
                artistImageHeight = 234,
                artistImageAttribution = "Some attribution"
            )
        )
    }

    @Test
    fun `test image not found `() {
        whenever(mockImageClient.getDimensions(any())).thenReturn(Mono.error(RuntimeException("Not found")))
        imagesEnrichmentService.enrichImagesForSongs()
        verify(mockSongRepository, after(240)).save(
            song.copy(
                status = SongStatus.INCOMPLETE,
                remarks = "Could not find file on url https://somephoto.com for Michelle with error type RuntimeException and message Not found"
            )
        )
    }

}
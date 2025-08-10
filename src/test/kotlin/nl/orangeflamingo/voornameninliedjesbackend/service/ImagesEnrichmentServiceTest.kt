package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
import org.springframework.data.jdbc.core.mapping.AggregateReference
import reactor.core.publisher.Mono
import java.net.URI
import java.util.Optional

class ImagesEnrichmentServiceTest {

    private val mockSongRepository = mockk<SongRepository>()
    private val mockArtistRepository = mockk<ArtistRepository>()
    private val mockImageClient = mockk<ImageClient>()
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
        photos = mutableSetOf(ArtistPhoto(url = URI.create("https://somephoto.com"), attribution = "Some attribution"))
    )

    @BeforeEach
    @Suppress("ReactiveStreamsUnusedPublisher")
    fun init() {
        every { mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code) } returns listOf(song)
        every { mockSongRepository.findAllByStatusAndArtistImageIsNullOrArtistImageAttributionIsNull(SongStatus.SHOW.code) } returns listOf(song)
        every { mockArtistRepository.findById(100) } returns Optional.of(artist)
        every { mockImageClient.getDimensions(any()) } returns Mono.just(ImageDimensionsDto("imageName", 234, 234))
    }

    @Test
    fun `test updateSong`() {
        imagesEnrichmentService.enrichImagesForSongs(true)
        verify { mockSongRepository.findAllByStatusOrderedByNameAndTitle("SHOW") }
        verify(timeout = 1000) {
            mockSongRepository.save(
                song.copy(
                    artistImage = "https://somephoto.com",
                    artistImageWidth = 234,
                    artistImageHeight = 234,
                    artistImageAttribution = "Some attribution"
                )
            )
        }
    }

    @Test
    fun `test enrichSong`() {
        imagesEnrichmentService.enrichImagesForSongs()
        verify(timeout = 500) {
            mockSongRepository.save(
                song.copy(
                    artistImage = "https://somephoto.com",
                    artistImageWidth = 234,
                    artistImageHeight = 234,
                    artistImageAttribution = "Some attribution"
                )
            )
        }
    }

    @Test
    @Suppress("ReactiveStreamsUnusedPublisher")
    fun `test image not found `() {
        every { mockImageClient.getDimensions(any()) } returns Mono.error(RuntimeException("Not found"))
        imagesEnrichmentService.enrichImagesForSongs()
        verify(timeout = 240) {
            mockSongRepository.save(
                song.copy(
                    status = SongStatus.INCOMPLETE,
                    remarks = "Could not find file on url https://somephoto.com for Michelle with error type RuntimeException and message Not found"
                )
            )
        }
    }

}
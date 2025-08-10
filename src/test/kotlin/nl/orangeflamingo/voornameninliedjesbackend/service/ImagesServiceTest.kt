package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.apache.commons.codec.binary.Base64
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.jdbc.core.mapping.AggregateReference
import reactor.core.publisher.Mono
import java.io.IOException
import java.util.Optional

class ImagesServiceTest {

    private val mockSongRepository = mockk<SongRepository>()
    private val mockArtistRepository = mockk<ArtistRepository>()
    private val mockImageClient = mockk<ImageClient>()
    private val imagesService = ImagesService(
        mockSongRepository,
        mockArtistRepository,
        mockImageClient
    )
    private val songWithoutArtistImage = Song(
        id = 1,
        title = "Michelle",
        name = "Michelle",
        artist = AggregateReference.to(100),
        status = SongStatus.SHOW
    )
    private val song = songWithoutArtistImage.copy(
        title = "Hey Jude",
        name = "Jude",
        artistImage = "https://remote-image.jpg"
    )

    private val artist = Artist(
        id = 100,
        name = "The Beatles"
    )

    @BeforeEach
    @Suppress("ReactiveStreamsUnusedPublisher")
    fun init() {
        every { mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code) } returns listOf(song)
        every { mockArtistRepository.findById(100) } returns Optional.of(artist)
        every { mockImageClient.downloadImage(any(), any(), any()) } returns Mono.just("image")
        every { mockImageClient.createImageBlur(any(), any(), any()) } returns
                Mono.just(
                    ImageHashDto(
                        "imageName",
                        "hashString"
                    )
                )
    }

    @Test
    fun `test download images`() {
        imagesService.downloadImages()
        verify(timeout = 120) { mockSongRepository.findAllByStatusOrderedByNameAndTitle("SHOW") }
        verify(timeout = 120) {
            mockSongRepository.save(
                song.copy(
                    localImage = "the-beatles_hey-jude.jpg"
                )
            )
        }
    }

    @Test
    fun `test download image for song`() {
        imagesService.downloadImageForSong(song)
        verify(timeout = 120) {
            mockSongRepository.save(
                song.copy(
                    localImage = "the-beatles_hey-jude.jpg"
                )
            )
        }
    }

    @Test
    fun `test blur image for song`() {
        val imageUrl = "https://theimage"
        imagesService.blurImageForSong(song.copy(artistImage = imageUrl))
        verify(timeout = 120) {
            mockSongRepository.save(
                match { song -> Base64.isBase64(song.blurredImage) }
            )
        }
    }

    @Test
    fun `test blur images`() {
        every { mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code) } returns
                listOf(song.copy(blurredImage = "some-image-blur"))
        imagesService.blurImages()
        verify(timeout = 120) { mockSongRepository.findAllByStatusOrderedByNameAndTitle("SHOW") }
        verify(exactly = 0) { mockSongRepository.save(any()) }
    }

    @Test
    fun `test artist not present for song`() {
        val artistId = 100L
        every { mockArtistRepository.findById(artistId) } returns Optional.empty()
        assertThatThrownBy { imagesService.downloadImageForSong(songWithoutArtistImage) }
            .isInstanceOf(ArtistNotFoundException::class.java)
            .hasMessage("Artist with id $artistId for song with title Michelle not found")

    }

    @Test
    fun `test artistId not present`() {
        val aggregateReference: AggregateReference<Artist, Long> = mockk()
        val songWithoutArtistId = Song(
            id = 10,
            title = "Gloria",
            name = "Gloria",
            artist = aggregateReference,
            status = SongStatus.SHOW
        )
        every { aggregateReference.id } returns null
        assertThatThrownBy { imagesService.downloadImageForSong(songWithoutArtistId) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `test artist image not present for song`() {
        imagesService.downloadImageForSong(songWithoutArtistImage)
        verify(exactly = 0) { mockSongRepository.save(any()) }
    }

    @Test
    @Suppress("ReactiveStreamsUnusedPublisher")
    fun `test image api client throws exception `() {
        every { mockImageClient.downloadImage(any(), any(), any()) } returns Mono.error(IOException("error downloading"))
        imagesService.downloadImageForSong(song)
        verify(exactly = 0) { mockSongRepository.save(any()) }
    }

}
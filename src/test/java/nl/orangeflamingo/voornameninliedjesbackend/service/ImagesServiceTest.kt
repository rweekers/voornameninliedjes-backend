package nl.orangeflamingo.voornameninliedjesbackend.service

import java.io.IOException
import java.util.Optional
import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.apache.commons.codec.binary.Base64
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.after
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.whenever
import org.springframework.data.jdbc.core.mapping.AggregateReference
import reactor.core.publisher.Mono

class ImagesServiceTest {

    private val mockSongRepository = mock(SongRepository::class.java)
    private val mockArtistRepository = mock(ArtistRepository::class.java)
    private val mockImageClient = mock(ImageClient::class.java)
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
    fun init() {
        whenever(mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)).thenReturn(
            listOf(song)
        )
        whenever(mockArtistRepository.findById(100)).thenReturn(Optional.of(artist))
        whenever(mockImageClient.downloadImage(any(), any(), any())).thenReturn(Mono.just("bla"))
        whenever(mockImageClient.createImageBlur(any(), any(), any())).thenReturn(
            Mono.just(
                ImageHashDto(
                    "imageName",
                    "hashString"
                )
            )
        )
    }

    @Test
    fun `test download images`() {
        imagesService.downloadImages()
        verify(mockSongRepository, after(120)).findAllByStatusOrderedByNameAndTitle("SHOW")
        verify(mockSongRepository, after(120)).save(
            song.copy(
                localImage = "the-beatles_hey-jude.jpg"
            )
        )
    }

    @Test
    fun `test download image for song`() {
        imagesService.downloadImageForSong(song)
        verify(mockSongRepository, after(120)).save(
            song.copy(
                localImage = "the-beatles_hey-jude.jpg"
            )
        )
    }

    @Test
    fun `test blur image for song`() {
        val imageUrl = "https://theimage"
        imagesService.blurImageForSong(song.copy(artistImage = imageUrl))
        verify(mockSongRepository, after(120)).save(
            argThat { song -> Base64.isBase64(song.blurredImage) }
        )
    }

    @Test
    fun `test blur images`() {
        whenever(mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)).thenReturn(
            listOf(song.copy(blurredImage = "some-image-blur"))
        )
        imagesService.blurImages()
        verify(mockSongRepository, after(120)).findAllByStatusOrderedByNameAndTitle("SHOW")
        verify(mockSongRepository, never()).save(any())
    }

    @Test
    fun `test artist not present for song`() {
        whenever(mockArtistRepository.findById(100)).thenReturn(Optional.empty())
        assertThrows<ArtistNotFoundException> { imagesService.downloadImageForSong(songWithoutArtistImage) }
    }

    @Test
    fun `test artistId not present`() {
        val aggregateReference: AggregateReference<Artist, Long> = mock(AggregateReference::class.java) as AggregateReference<Artist, Long>
        val songWithoutArtistId = Song(
            id = 10,
            title = "Gloria",
            name = "Gloria",
            artist = aggregateReference,
            status = SongStatus.SHOW
        )
        whenever(aggregateReference.id).thenReturn(null)
        assertThrows<IllegalStateException> { imagesService.downloadImageForSong(songWithoutArtistId) }
    }

    @Test
    fun `test artist image not present for song`() {
        imagesService.downloadImageForSong(songWithoutArtistImage)
        verify(mockSongRepository, never()).save(any())
    }

    @Test
    fun `test image api client throws exception `() {
        whenever(mockImageClient.downloadImage(any(), any(), any())).thenReturn(Mono.error(IOException("error downloading")))
        imagesService.downloadImageForSong(song)
        verify(mockSongRepository, never()).save(any())
    }

}
package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistRef
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
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
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import reactor.core.publisher.Mono
import java.io.IOException
import java.util.Optional

class ImagesServiceTest {

    private val mockSongRepository = mock(SongRepository::class.java)
    private val mockArtistRepository = mock(ArtistRepository::class.java)
    private val mockFileService = mock(FileService::class.java)
    private val mockImageAptClient = mock(ImageApiClient::class.java)
    private val imagesService = ImagesService(
        mockSongRepository,
        mockArtistRepository,
        mockFileService,
        mockImageAptClient
    )
    private val songWithoutArtistImage = Song(
        id = 1,
        title = "Michelle",
        name = "Michelle",
        artists = mutableSetOf(ArtistRef(100)),
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
        `when`(mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)).thenReturn(
            listOf(song)
        )
        `when`(mockArtistRepository.findById(100)).thenReturn(Optional.of(artist))
        `when`(mockFileService.fileExists("the-beatles_hey-jude.jpg")).thenReturn(false)
    }

    @Test
    fun `test download images`() {
        imagesService.downloadImages()
        verify(mockSongRepository, after(120)).findAllByStatusOrderedByNameAndTitle("SHOW")
        verify(mockFileService, after(120)).fileExists("images/the-beatles_hey-jude.jpg")
        verify(mockFileService, after(120)).writeToDisk("https://remote-image.jpg", "images/the-beatles_hey-jude.jpg")
        verify(mockSongRepository, after(120)).save(
            song.copy(
                localImage = "the-beatles_hey-jude.jpg"
            )
        )
    }

    @Test
    fun `test download image for song`() {
        imagesService.downloadImageForSong(song)
        verify(mockFileService).fileExists("images/the-beatles_hey-jude.jpg")
        verify(mockFileService).writeToDisk("https://remote-image.jpg", "images/the-beatles_hey-jude.jpg")
        verify(mockSongRepository).save(
            song.copy(
                localImage = "the-beatles_hey-jude.jpg"
            )
        )
    }

    @Test
    fun `test blur image for song`() {
        val imageUrl = "https://theimage"
        val blurredImage = "blur"
        `when`(mockImageAptClient.createBlurString(imageUrl, 10, 10)).thenReturn(Mono.just(blurredImage))
        imagesService.blurImageForSong(song.copy(artistImage = imageUrl))
        verify(mockSongRepository).save(
            argThat { song -> Base64.isBase64(song.blurredImage) }
        )
    }

    @Test
    fun `test blur images`() {
        `when`(mockSongRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)).thenReturn(
            listOf(song.copy(blurredImage = "some-image-blur"))
        )
        imagesService.blurImages()
        verify(mockSongRepository, after(120)).findAllByStatusOrderedByNameAndTitle("SHOW")
        verify(mockSongRepository, never()).save(any())
    }

    @Test
    fun `test image already present for song`() {
        `when`(mockFileService.fileExists("images/the-beatles_hey-jude.jpg")).thenReturn(true)
        imagesService.downloadImageForSong(song)
        verify(mockFileService).fileExists("images/the-beatles_hey-jude.jpg")
        verify(mockFileService, never()).writeToDisk(any(), any())
        verify(mockSongRepository, never()).save(any())
    }

    @Test
    fun `test image already present for song with override`() {
        `when`(mockFileService.fileExists("images/the-beatles_hey-jude.jpg")).thenReturn(true)
        imagesService.downloadImageForSong(song, true)
        verify(mockFileService, never()).fileExists(any())
        verify(mockFileService).writeToDisk("https://remote-image.jpg", "images/the-beatles_hey-jude.jpg")
        verify(mockSongRepository).save(
            song.copy(
                localImage = "the-beatles_hey-jude.jpg"
            )
        )
    }

    @Test
    fun `test artist not present for song`() {
        `when`(mockArtistRepository.findById(100)).thenReturn(Optional.empty())
        assertThrows<ArtistNotFoundException> { imagesService.downloadImageForSong(songWithoutArtistImage) }
    }

    @Test
    fun `test artist image not present for song`() {
        imagesService.downloadImageForSong(songWithoutArtistImage)
        verify(mockFileService, never()).fileExists(any())
        verify(mockFileService, never()).writeToDisk(any(), any())
        verify(mockSongRepository, never()).save(any())
    }

    @Test
    fun `test file service throws exception `() {
        `when`(mockFileService.writeToDisk(any(), any())).thenThrow(
            IOException::class.java
        )
        imagesService.downloadImageForSong(song)
        verify(mockFileService).fileExists("images/the-beatles_hey-jude.jpg")
        verify(mockFileService).writeToDisk("https://remote-image.jpg", "images/the-beatles_hey-jude.jpg")
        verify(mockSongRepository, never()).save(any())
    }

}
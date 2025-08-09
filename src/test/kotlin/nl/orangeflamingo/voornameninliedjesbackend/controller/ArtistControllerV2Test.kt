package nl.orangeflamingo.voornameninliedjesbackend.controller

import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.model.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArtistControllerV2Test {

    private val artistService: ArtistService = mockk()

    private val artistControllerV2 = ArtistsControllerV2(artistService)
    private val artist = Artist(1, "Test", UUID.randomUUID())

    @Test
    fun `get all artists`() {
        every {
            artistService.findAllOrderedByName()
        } returns listOf(artist)
        val artists = artistControllerV2.getApiArtists()
        assertThat(artists.body).isEqualTo(
                listOf(
                    ArtistDto(
                        id = artist.id ?: throw IllegalStateException(),
                        name = artist.name,
                        imageUrl = null,
                        photos = emptyList()
                    ),
                )
            )
    }

    @Test
    fun `get artist by id`() {
        every { artistService.findById(1L) } returns artist
        val artistResponse = artistControllerV2.getArtistById(1)
        assertThat(artistResponse.body).isEqualTo(
            ArtistDto(
                id = artist.id ?: throw IllegalStateException(),
                name = artist.name,
                imageUrl = null,
                photos = emptyList()
            )
        )
    }
}
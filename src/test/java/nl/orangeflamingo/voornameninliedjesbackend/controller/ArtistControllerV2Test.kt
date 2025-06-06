package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.model.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.net.URI
import java.util.UUID

class ArtistControllerV2Test {

    private val artistService = mock(ArtistService::class.java)

    private val artistControllerV2 = ArtistsControllerV2(artistService)
    private val artist = Artist(1, "Test", UUID.randomUUID().toString())

    @Test
    fun `get all artists`() {
        whenever(artistService.findAllOrderedByName())
            .thenReturn(listOf(artist))
        val artists = artistControllerV2.apiArtists
        assertThat(artists.body).isEqualTo(
                listOf(
                    ArtistDto(artist.id, artist.name)
                        .imageUrl(URI.create(""))
                )
            )
    }

    @Test
    fun `get artist by id`() {
        whenever(artistService.findById(1L)).thenReturn(artist)
        val artistResponse = artistControllerV2.getArtistById(1)
        assertThat(artistResponse.body).isEqualTo(
            ArtistDto(artist.id, artist.name)
                .imageUrl(URI.create("")))
    }
}
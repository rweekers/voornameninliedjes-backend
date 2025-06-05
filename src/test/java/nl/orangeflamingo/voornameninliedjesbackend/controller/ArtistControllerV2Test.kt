package nl.orangeflamingo.voornameninliedjesbackend.controller

import java.net.URI
import java.util.Optional
import java.util.UUID
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class ArtistControllerV2Test {

    private val artistRepository = mock(ArtistRepository::class.java)

    private val artistControllerV2 = ArtistsControllerV2(artistRepository)
    private val artist = Artist(1, "Test", UUID.randomUUID().toString())

    @Test
    fun `get all artists`() {
        whenever(artistRepository.findAllOrderedByName())
            .thenReturn(listOf(artist))
        val artists = artistControllerV2.apiArtists
        assertThat(artists.body).isEqualTo(
                listOf(
                    nl.orangeflamingo.voornameninliedjesbackend.model.Artist(artist.id, artist.name)
                        .imageUrl(URI.create(""))
                )
            )
    }

    @Test
    fun `get artist by id`() {
        whenever(artistRepository.findById(1L)).thenReturn(Optional.of(artist))
        val artistResponse = artistControllerV2.getArtistById(1)
        assertThat(artistResponse.body).isEqualTo(
            nl.orangeflamingo.voornameninliedjesbackend.model.Artist(artist.id, artist.name)
                .imageUrl(URI.create("")))
    }
}
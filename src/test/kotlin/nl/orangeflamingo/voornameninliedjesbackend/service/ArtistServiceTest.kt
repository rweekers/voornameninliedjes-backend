package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional
import java.util.UUID
import java.util.stream.Stream

class ArtistControllerV2Test {

    private val repository = mock(ArtistRepository::class.java)

    private val artistService = ArtistService(repository)

    @ParameterizedTest
    @MethodSource("artistsProvider")
    fun `get artist by id`(artistOptional : Optional<Artist>, artist: Artist?) {
        val id = 1L
        whenever(repository.findById(id)).thenReturn(artistOptional)
        val artistFound = artistService.findById(id)
        assertThat(artistFound).isEqualTo(artist)
    }

    companion object {
        @JvmStatic
        fun artistsProvider(): Stream<Arguments> {
            val artist = Artist(1L, "The Beatles", UUID.randomUUID())
            return Stream.of(Arguments.of(Optional.ofNullable(null), null), Arguments.of(Optional.of(artist), artist))
        }
    }

    @Test
    fun `get artist by name`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        whenever(repository.findByNameIgnoreCase(artist.name)).thenReturn(listOf(artist))
        val artistFound = artistService.findByName(artist.name)
        assertThat(artistFound).isEqualTo(listOf(artist))
    }

    @Test
    fun `create artist`() {
        val artistName = "Rolling Stones"
        val artist = Artist(name = artistName, mbid = UUID.randomUUID())
        val id = 1L
        val savedArtist = artist.copy(id = id)

        whenever(repository.findFirstByName(artistName)).thenReturn(null)
        whenever(repository.save(artist)).thenReturn(savedArtist)

        val persistedArtist = artistService.create(artist)
        verify(repository).save(artist)
        assertThat(persistedArtist.id).isEqualTo(id)
    }

    @Test
    fun `create existing artist`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        val updatedArtist = artist.copy()
        whenever(repository.findFirstByName(updatedArtist.name)).thenReturn(artist)
        assertThatThrownBy { artistService.create(updatedArtist) }
            .isInstanceOf(DuplicateArtistNameException::class.java)
            .hasMessage("${updatedArtist.name} already exists")
    }

    @Test
    fun `update existing artist`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        val updatedArtist = artist.copy(name = "Beatles")
        whenever(repository.findById(1L)).thenReturn(Optional.of(artist))
        whenever(repository.save(updatedArtist)).thenReturn(updatedArtist)
        val persistedArtist = artistService.update(1L, updatedArtist)
        verify(repository).save(updatedArtist)
        assertThat(persistedArtist.name).isEqualTo("Beatles")
    }

}
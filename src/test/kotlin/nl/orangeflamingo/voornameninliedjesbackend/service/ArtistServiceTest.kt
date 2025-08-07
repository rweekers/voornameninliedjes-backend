package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ArtistServiceTest {

    private val repository = mockk<ArtistRepository>()

    private val artistService = ArtistService(repository)

    @Test
    fun `get artist by id`() {
        val id = 1L
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        every { repository.findById(id) } returns Optional.of(artist)
        val artistFound = artistService.findById(id)
        assertThat(artistFound).isEqualTo(artist)
    }

    @Test
    fun `artist not found`() {
        every { repository.findById(any()) } returns Optional.empty()
        assertThrows<ArtistNotFoundException> { artistService.findById(1) }
    }

    @Test
    fun `get artist by name`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        every { repository.findByNameIgnoreCase(artist.name) } returns listOf(artist)
        val artistFound = artistService.findByName(artist.name)
        assertThat(artistFound).isEqualTo(listOf(artist))
    }

    @Test
    fun `create artist`() {
        val artistName = "Rolling Stones"
        val artist = Artist(name = artistName, mbid = UUID.randomUUID())
        val id = 1L
        val savedArtist = artist.copy(id = id)

        every { repository.findFirstByName(artistName) } returns null
        every { repository.save(artist) } returns savedArtist

        val persistedArtist = artistService.create(artist)
        verify { repository.save(artist) }
        assertThat(persistedArtist.id).isEqualTo(id)
    }

    @Test
    fun `create existing artist`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        val updatedArtist = artist.copy()
        every { repository.findFirstByName(updatedArtist.name) } returns artist
        assertThatThrownBy { artistService.create(updatedArtist) }
            .isInstanceOf(DuplicateArtistNameException::class.java)
            .hasMessage("${updatedArtist.name} already exists")
    }

    @Test
    fun `update existing artist`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        val updatedArtist = artist.copy(name = "Beatles")
        every { repository.findById(1L) } returns Optional.of(artist)
        every { repository.save(updatedArtist) } returns updatedArtist
        every { repository.findFirstByName(updatedArtist.name) } returns null
        val persistedArtist = artistService.update(1L, updatedArtist)
        verify { repository.save(updatedArtist) }
        assertThat(persistedArtist.name).isEqualTo("Beatles")
    }

}
package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import nl.orangeflamingo.voornameninliedjesbackend.command.ArtistPhotoCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.UpdateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.CurrentUser
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import tools.jackson.databind.ObjectMapper
import java.net.URI
import java.util.Optional
import java.util.UUID

class ArtistServiceTest {

    private val repository = mockk<ArtistRepository>()
    private val currentUserService = mockk<CurrentUserService>()
    private val objectMapper = mockk<ObjectMapper>()

    private val artistService = ArtistService(repository, currentUserService, objectMapper)

    @Test
    fun `get artist by id`() {
        val id = 1L
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        every { repository.findById(id) } returns Optional.of(artist)
        val artistFound = artistService.findById(id)
        assertThat(artistFound).isEqualTo(artist)
    }

    @Test
    fun `get all artists`() {
        val artist1 = Artist(1L, "The Beatles", UUID.randomUUID())
        val artist2 = Artist(1L, "David Bowie", UUID.randomUUID())
        every { repository.findAllOrderedByName() } returns listOf(artist1, artist2)
        val artists = artistService.findAllOrderedByName()
        assertThat(artists).isEqualTo(listOf(artist1, artist2))
    }

    @ParameterizedTest
    @CsvSource(
        "David, 1",
        "'', 0"
    )
    fun `search artists`(search: String, resultSize: Int) {
        val artist = Artist(1L, "David Bowie", UUID.randomUUID())
        every { repository.findByNameContainingIgnoreCase(search) } returns listOf(artist)
        val artists = artistService.search(search)
        assertThat(artists).hasSize(resultSize)
    }

    @Test
    fun `artist not found`() {
        every { repository.findById(any()) } returns Optional.empty()
        val artistId = 1L
        assertThatThrownBy { artistService.findById(artistId) }
            .isInstanceOf(ArtistNotFoundException::class.java)
            .hasMessage("Artist with id $artistId not found")
    }

    @Test
    fun `get artist by name`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        every { repository.findByNameIgnoreCase(artist.name) } returns listOf(artist)
        val artistFound = artistService.findByName(artist.name)
        assertThat(artistFound).isEqualTo(listOf(artist))
    }

    @Test
    fun `create artist v1`() {
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
    fun `create existing artist v1`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        val updatedArtist = artist.copy()
        every { repository.findFirstByName(updatedArtist.name) } returns artist
        assertThatThrownBy { artistService.create(updatedArtist) }
            .isInstanceOf(DuplicateArtistNameException::class.java)
            .hasMessage("${updatedArtist.name} already exists")
    }

    @Test
    fun `update existing artist v1`() {
        val artist = Artist(1L, "The Beatles", UUID.randomUUID())
        val updatedArtist = artist.copy(name = "Beatles")
        every { repository.findById(1L) } returns Optional.of(artist)
        every { repository.save(updatedArtist) } returns updatedArtist
        every { repository.findFirstByName(updatedArtist.name) } returns null
        val persistedArtist = artistService.update(1L, updatedArtist)
        verify { repository.save(updatedArtist) }
        assertThat(persistedArtist.name).isEqualTo("Beatles")
    }

    @Test
    fun `create artist`() {
        val artist = Artist(
            name = "The Beatles",
            mbid = UUID.randomUUID(),
            photos = mutableSetOf(ArtistPhoto(1L, URI("http://foto.jpg"), "attribution"))
        )
        val createArtistCommand = createArtistCommand(artist)
        every { repository.findById(1L) } returns Optional.of(artist)
        every { repository.findFirstByName(artist.name) } returns null
        every { currentUserService.currentUser() } returns CurrentUser(UUID.randomUUID().toString(), "admin-user")
        every { objectMapper.writeValueAsString(createArtistCommand) } returns """{"id":1}"""
        every { repository.save(any()) } answers { firstArg() }
        artistService.create(createArtistCommand)
        val savedArtist = slot<Artist>()
        verify {
            repository.save(capture(savedArtist))
        }
        assertThat(savedArtist.captured.name).isEqualTo("The Beatles")
        assertThat(savedArtist.captured.background).isNull()
        assertThat(savedArtist.captured.photos).hasSize(1)
    }

    @Test
    fun `create existing artist`() {
        val artist = Artist(name = "Beatles", mbid = UUID.randomUUID())
        val createArtistCommand = createArtistCommand(artist)
        every { repository.findFirstByName(createArtistCommand.name) } returns artist
        assertThrows(DuplicateArtistNameException::class.java) {
            artistService.create(createArtistCommand)
        }
    }

    @Test
    fun `update existing artist`() {
        val artist = Artist(
            1L,
            "The Beatles",
            UUID.randomUUID(),
            photos = mutableSetOf(ArtistPhoto(1L, URI("http://foto.jpg"), "attribution"))
        )
        val updatedArtist = artist.copy(name = "Beatles")
        val updateArtistCommand = updateArtistCommand(updatedArtist)
        every { repository.findById(1L) } returns Optional.of(artist)
        every { repository.existsByNameAndIdNot(updatedArtist.name, updateArtistCommand.id) } returns false
        every { currentUserService.currentUser() } returns CurrentUser(UUID.randomUUID().toString(), "admin-user")
        every { objectMapper.writeValueAsString(updateArtistCommand) } returns """{"id":1}"""
        every { repository.save(updatedArtist) } returns updatedArtist

        artistService.update(updateArtistCommand)
        val savedArtist = slot<Artist>()
        verify {
            repository.save(capture(savedArtist))
        }
        assertThat(savedArtist.captured.name).isEqualTo("Beatles")
        assertThat(savedArtist.captured.logEntries)
            .singleElement()
            .extracting(ArtistLogEntry::username)
            .isEqualTo("admin-user")
    }

    @Test
    fun `update existing artist to name other existing artist`() {
        val artist = Artist(1L, "Beatles", UUID.randomUUID())
        val updatedArtist = artist.copy(name = "The Beatles")
        val updateArtistCommand = updateArtistCommand(updatedArtist)
        every { repository.findById(1L) } returns Optional.of(artist)
        every { repository.existsByNameAndIdNot(updatedArtist.name, updateArtistCommand.id) } returns true
        assertThrows(DuplicateArtistNameException::class.java) {
            artistService.update(updateArtistCommand)
        }
    }

    @Test
    fun `delete artist`() {
        val id = 1L
        every { repository.existsById(eq(id)) } returns true
        every { repository.deleteById(id) } just Runs
        artistService.delete(id)
        verify { repository.deleteById(id) }
    }

    @Test
    fun `delete unknown artist`() {
        val id = 1L
        every { repository.existsById(id) } returns false
        assertThrows(ArtistNotFoundException::class.java) {
            artistService.delete(id)
        }
    }

    private fun createArtistCommand(artist: Artist): CreateArtistCommand {
        return CreateArtistCommand(
            name = artist.name,
            mbid = artist.mbid,
            background = artist.background,
            lastFmUrl = artist.lastFmUrl,
            photos = artist.photos.map { ArtistPhotoCommand(it.url, it.attribution) }.toSet(),
        )
    }

    private fun updateArtistCommand(artist: Artist): UpdateArtistCommand {
        return UpdateArtistCommand(
            id = artist.id ?: throw IllegalStateException(),
            name = artist.name,
            mbid = artist.mbid,
            background = artist.background,
            lastFmUrl = artist.lastFmUrl,
            photos = artist.photos.map { ArtistPhotoCommand(it.url, it.attribution) }.toSet(),
        )
    }
}
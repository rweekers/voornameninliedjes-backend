package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.UpdateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.Jsonb
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.time.Instant

@Service
class ArtistService(
    private val repository: ArtistRepository,
    private val currentUserService: CurrentUserService,
    private val httpRequestContext: HttpRequestContext,
    private val objectMapper: ObjectMapper
) {

    fun findByName(name: String): List<Artist> {
        return repository.findByNameIgnoreCase(name)
    }

    fun findAllOrderedByName(): List<Artist> {
        return repository.findAllOrderedByName()
    }


    fun create(artist: Artist): Artist {
        if (existsByName(artist.name)) {
            throw DuplicateArtistNameException("${artist.name} already exists")
        }
        return repository.save(artist)
    }

    fun update(id: Long, updated: Artist): Artist {
        val existing = repository.findById(id)
            .orElseThrow { ArtistNotFoundException(id) }

        val artistWithSameName = repository.findFirstByName(updated.name)
        if (artistWithSameName != null && artistWithSameName.id != id) {
            throw DuplicateArtistNameException(updated.name)
        }

        val updatedEntity = existing.copy(name = updated.name, photos = updated.photos)

        return repository.save(updatedEntity)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw ArtistNotFoundException(id)
        }
        repository.deleteById(id)
    }

    fun findById(id: Long): Artist =
        repository.findById(id).orElseThrow { ArtistNotFoundException(id) }

    private fun existsByName(name: String): Boolean =
        repository.findFirstByName(name) != null

    private fun existsByName(name: String, excludeId: Long): Boolean {
        return repository.existsByNameAndIdNot(name, excludeId)
    }

    fun update(updateArtistCommand: UpdateArtistCommand): Artist {
        val artist = repository.findById(updateArtistCommand.id)
            .orElseThrow { ArtistNotFoundException(updateArtistCommand.id) }

        if (existsByName(updateArtistCommand.name, updateArtistCommand.id)) {
            throw DuplicateArtistNameException(
                "${updateArtistCommand.name} already exists"
            )
        }

        val currentUser = currentUserService.currentUser()

        artist.name = updateArtistCommand.name
        artist.background = updateArtistCommand.background
        artist.mbid = updateArtistCommand.mbid
        artist.lastFmUrl = updateArtistCommand.lastFmUrl

        artist.photos.clear()
        artist.photos.addAll(
            updateArtistCommand.photos.map {
                ArtistPhoto(
                    url = it.url,
                    attribution = it.attribution
                )
            }
        )

        artist.logEntries.add(
            ArtistLogEntry(
                date = Instant.now(),
                username = currentUser.username,
                userId = currentUser.id,
                httpMethod = httpRequestContext.method(),
                request = Jsonb(objectMapper.writeValueAsString(updateArtistCommand))
            )
        )

        return repository.save(artist)
    }

    fun create(createArtistCommand: CreateArtistCommand): Artist {
        if (existsByName(createArtistCommand.name)) {
            throw DuplicateArtistNameException(
                "${createArtistCommand.name} already exists"
            )
        }

        val currentUser = currentUserService.currentUser()

        val artist = Artist(
            name = createArtistCommand.name,
            background = createArtistCommand.background,
            mbid = createArtistCommand.mbid,
            lastFmUrl = createArtistCommand.lastFmUrl,
            photos = createArtistCommand.photos
                .map {
                    ArtistPhoto(
                        url = it.url,
                        attribution = it.attribution
                    )
                }
                .toMutableSet(),
            logEntries = mutableSetOf(
                ArtistLogEntry(
                    date = Instant.now(),
                    username = currentUser.username,
                    userId = currentUser.id,
                    httpMethod = httpRequestContext.method(),
                    request = Jsonb(objectMapper.writeValueAsString(createArtistCommand))
                )
            )
        )

        return repository.save(artist)
    }

}
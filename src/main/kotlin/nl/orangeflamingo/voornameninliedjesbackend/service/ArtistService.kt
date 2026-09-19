package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.UpdateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.springframework.stereotype.Service

@Service
class ArtistService(
    private val repository: ArtistRepository,
    private val currentUserService: CurrentUserService
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

    fun update(updateArtistCommand: UpdateArtistCommand): Artist {
        val existing = repository.findById(updateArtistCommand.id)
            .orElseThrow { ArtistNotFoundException(updateArtistCommand.id) }

        val artistWithSameName = repository.findFirstByName(updateArtistCommand.name)
        if (artistWithSameName != null && artistWithSameName.id != updateArtistCommand.id) {
            throw DuplicateArtistNameException(updateArtistCommand.name)
        }

        val updatedEntity = existing.copy(name = updateArtistCommand.name)

        return repository.save(updatedEntity)

    }

    fun create(createArtistCommand: CreateArtistCommand): Artist {
        if (existsByName(createArtistCommand.name)) {
            throw DuplicateArtistNameException("${createArtistCommand.name} already exists")
        }
        return repository.save(Artist(
            name = createArtistCommand.name,
            background = createArtistCommand.background
        ))
    }

}
package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.springframework.stereotype.Service

@Service
class ArtistService(private val repository: ArtistRepository) {

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

}
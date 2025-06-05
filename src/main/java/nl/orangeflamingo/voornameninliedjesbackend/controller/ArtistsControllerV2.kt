package nl.orangeflamingo.voornameninliedjesbackend.controller

import jakarta.validation.Valid
import nl.orangeflamingo.voornameninliedjesbackend.api.ArtistsApi
import nl.orangeflamingo.voornameninliedjesbackend.model.Artist
import nl.orangeflamingo.voornameninliedjesbackend.model.NewArtist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class ArtistsControllerV2(private val artistRepository: ArtistRepository) : ArtistsApi {

    private val log = LoggerFactory.getLogger(ArtistsControllerV2::class.java)

    override fun getApiArtists(): ResponseEntity<List<Artist>> {
        log.info("Requesting all artists v2...")
        return ResponseEntity.ok(artistRepository.findAllOrderedByName().map { toArtistMessage(it) })
    }

    override fun createArtist(newArtist: @Valid NewArtist): ResponseEntity<Artist> {
        return ResponseEntity.ok(toArtistMessage(artistRepository.save(toDomain(newArtist))))
    }

    @Suppress("kotlin:S6508") // Implement Java interface generated from OpenAPI definition
    override fun deleteArtist(artistId: Long): ResponseEntity<Void> {
        artistRepository.deleteById(artistId)
        return ResponseEntity.noContent().build()
    }

    override fun getArtistById(artistId: Long): ResponseEntity<Artist> {
        return artistRepository.findById(artistId)
            .map { ResponseEntity.ok(toArtistMessage(it)) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    override fun updateArtist(
        artistId: Long,
        newArtist: @Valid NewArtist
    ): ResponseEntity<Artist> {
        val artist = artistRepository.findById(artistId).orElse(null)
            ?: return ResponseEntity.notFound().build()

        artist.name = newArtist.name
        val persistedArtist = artistRepository.save(artist)

        return ResponseEntity.ok(toArtistMessage(persistedArtist))
    }

    private fun toDomain(artistMessage: NewArtist): nl.orangeflamingo.voornameninliedjesbackend.domain.Artist {
        return nl.orangeflamingo.voornameninliedjesbackend.domain.Artist(
            name = artistMessage.name
        )
    }

    private fun toArtistMessage(artist: nl.orangeflamingo.voornameninliedjesbackend.domain.Artist): Artist {
        return Artist.builder()
            .id(artist.id)
            .name(artist.name)
            .imageUrl(URI.create(artist.photos.firstOrNull()?.url ?: ""))
            .build()
    }

}
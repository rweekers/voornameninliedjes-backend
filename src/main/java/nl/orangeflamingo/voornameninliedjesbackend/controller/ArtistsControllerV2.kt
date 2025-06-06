package nl.orangeflamingo.voornameninliedjesbackend.controller

import jakarta.validation.Valid
import nl.orangeflamingo.voornameninliedjesbackend.api.ArtistsApi
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.model.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.model.NewArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class ArtistsControllerV2(private val artistService: ArtistService) : ArtistsApi {

    private val log = LoggerFactory.getLogger(ArtistsControllerV2::class.java)

    override fun getApiArtists(): ResponseEntity<List<ArtistDto>> {
        log.info("Requesting all artists v2...")
        return ResponseEntity.ok(artistService.findAllOrderedByName().map { toArtistMessage(it) })
    }

    override fun createArtist(newArtist: @Valid NewArtistDto): ResponseEntity<ArtistDto> {
        return ResponseEntity.ok(toArtistMessage(artistService.create(toDomain(newArtist))))
    }

    @Suppress("kotlin:S6508") // Implement Java interface generated from OpenAPI definition
    override fun deleteArtist(artistId: Long): ResponseEntity<Void> {
        artistService.delete(artistId)
        return ResponseEntity.noContent().build()
    }

    override fun getArtistById(artistId: Long): ResponseEntity<ArtistDto> {
        val artist = artistService.findById(artistId) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(toArtistMessage(artist))
    }

    override fun updateArtist(
        artistId: Long,
        updateArtistDto: @Valid NewArtistDto
    ): ResponseEntity<ArtistDto?>? {
        val updatedArtist = Artist(name = updateArtistDto.name)
        val artist = artistService.update(artistId, updatedArtist)
        return ResponseEntity.ok(toArtistMessage(artist))
    }

    private fun toDomain(artistMessage: NewArtistDto): Artist {
        return Artist(
            name = artistMessage.name
        )
    }

    private fun toArtistMessage(artist: Artist): ArtistDto {
        return ArtistDto.builder()
            .id(artist.id)
            .name(artist.name)
            .imageUrl(URI.create(artist.photos.firstOrNull()?.url ?: ""))
            .build()
    }

}
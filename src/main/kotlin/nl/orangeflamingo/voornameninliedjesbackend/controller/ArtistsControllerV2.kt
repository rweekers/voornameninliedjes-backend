package nl.orangeflamingo.voornameninliedjesbackend.controller

import jakarta.validation.Valid
import nl.orangeflamingo.voornameninliedjesbackend.api.ArtistsApi
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.model.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.model.ArtistInputDto
import nl.orangeflamingo.voornameninliedjesbackend.model.PhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ArtistsControllerV2(private val artistService: ArtistService) : ArtistsApi {

    private val log = LoggerFactory.getLogger(ArtistsControllerV2::class.java)

    override fun getApiArtists(): ResponseEntity<List<ArtistDto>> {
        log.info("Requesting all artists v2...")
        return ResponseEntity.ok(artistService.findAllOrderedByName().map { toArtistMessage(it) })
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun createArtist(artistInputDto: @Valid ArtistInputDto): ResponseEntity<ArtistDto> {
        return ResponseEntity.ok(toArtistMessage(artistService.create(toDomain(artistInputDto))))
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteArtist(artistId: Long): ResponseEntity<Unit> {
        artistService.delete(artistId)
        return ResponseEntity.noContent().build()
    }

    override fun getArtistById(artistId: Long): ResponseEntity<ArtistDto> {
        return ResponseEntity.ok(toArtistMessage(artistService.findById(artistId)))
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateArtist(
        artistId: Long,
        artistInputDto: @Valid ArtistInputDto
    ): ResponseEntity<ArtistDto> {
        val updatedArtist = toDomain(artistInputDto)
        val artist = artistService.update(artistId, updatedArtist)
        return ResponseEntity.ok(toArtistMessage(artist))
    }

    private fun toDomain(artistMessage: ArtistInputDto): Artist {
        return Artist(
            name = artistMessage.name,
            photos = artistMessage.photos.map {
                ArtistPhoto(
                    url = it.url,
                    attribution = it.attribution
                )
            }.toMutableSet()
        )
    }

    private fun toArtistMessage(artist: Artist): ArtistDto {
        return ArtistDto(
            id = artist.id ?: throw IllegalStateException("Artist id is null"),
            name = artist.name,
            imageUrl = artist.photos.firstOrNull()?.url,
            photos = artist.photos.map { toPhoto(it) }
        )
    }

    private fun toPhoto(photo: ArtistPhoto): PhotoDto {
        return PhotoDto(photo.url, photo.attribution)
    }

}
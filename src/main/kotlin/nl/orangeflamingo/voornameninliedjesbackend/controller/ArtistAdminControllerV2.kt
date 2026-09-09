package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.UpdateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.config.ApiMediaTypes
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistInputDto
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/admin/artists"],
    produces = [ApiMediaTypes.ADMIN_ARTISTS_V2]
)
class ArtistAdminControllerV2(
    private val artistService: ArtistService
) {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    fun getArtists(): List<AdminArtistDto> {
        return artistService.findAllOrderedByName()
            .map { convertToDto(it) }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    fun getArtistById(
        @PathVariable id: Long
    ): AdminArtistDto {
        return convertToDto(artistService.findById(id))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createArtist(
        @RequestBody input: AdminArtistInputDto,
        authentication: Authentication
    ): AdminArtistDto {
        val command = CreateArtistCommand(
            name = input.name.trim(),
            background = input.background?.trim(),
            imageUrl = input.imageUrl?.trim(),
            imageAttribution = input.imageAttribution?.trim()
        )
        val artist = artistService.create(command)

        return convertToDto(artist)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(
        "/{id}",
        consumes = ["application/json"]
    )
    fun updateArtist(
        @PathVariable id: Long,
        @RequestBody input: AdminArtistInputDto,
        authentication: Authentication
    ): AdminArtistDto {
        val command = UpdateArtistCommand(
            id = id,
            name = input.name.trim(),
            background = input.background?.trim(),
            imageUrl = input.imageUrl?.trim(),
            imageAttribution = input.imageAttribution?.trim()
        )
        val artist = artistService.update(command)

        return convertToDto(artist)
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteArtist(
        @PathVariable id: Long
    ) {
        artistService.delete(id)
    }

    private fun convertToDto(artist: Artist): AdminArtistDto {
        return AdminArtistDto(
            id = artist.id,
            name = artist.name,
            background = artist.background,
            // wikimediaPhotos = artist.photos.map { convertToDto(it) }.toSet(),
            flickrPhotos = emptySet(),
            // logEntries = artist.logEntries.map { convertToDto(it) }
        )
    }

    // Existing conversion methods...
}
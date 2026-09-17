package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.command.ArtistPhotoCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.UpdateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.config.ApiMediaTypes
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistInputDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistLogEntryV2Dto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistPhotoV2Dto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistV2Dto
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getArtists(): List<AdminArtistV2Dto> {
        return artistService.findAllOrderedByName()
            .map { convertToDto(it) }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    fun getArtistById(
        @PathVariable id: Long
    ): AdminArtistV2Dto {
        return convertToDto(artistService.findById(id))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createArtist(
        @RequestBody input: AdminArtistInputDto
    ): AdminArtistV2Dto {
        val command = CreateArtistCommand(
            name = input.name.trim(),
            background = input.background?.trim(),
            mbid = input.mbid,
            lastFmUrl = input.lastFmUrl,
            photos = input.photos.map { ArtistPhotoCommand(it.imageUrl, it.imageAttribution.trim()) }.toSet()
        )
        val artist = artistService.create(command)

        return convertToDto(artist)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(
        "/{id}",
        consumes = ["application/json"]
    )
    fun updateArtist(
        @PathVariable id: Long,
        @RequestBody input: AdminArtistInputDto
    ): AdminArtistV2Dto {
        val command = UpdateArtistCommand(
            id = id,
            name = input.name.trim(),
            background = input.background?.trim(),
            mbid = input.mbid,
            lastFmUrl = input.lastFmUrl,
            photos = input.photos.map { ArtistPhotoCommand(it.imageUrl, it.imageAttribution.trim()) }.toSet()
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

    private fun convertToDto(artist: Artist): AdminArtistV2Dto {
        return AdminArtistV2Dto(
            id = artist.id,
            name = artist.name,
            background = artist.background,
            photos = artist.photos
                .map { AdminArtistPhotoV2Dto(it.url, it.attribution) }
                .toSet(),
            logEntries = artist.logEntries
                .map { AdminArtistLogEntryV2Dto(it.date, it.username, it.userId, it.httpMethod, it.request?.value) }
                .toList()
        )
    }
}
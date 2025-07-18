package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistLogEntryDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistWikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class ArtistAdminController(private val artistRepository: ArtistRepository, private val artistService: ArtistService) {

    private val log = LoggerFactory.getLogger(ArtistAdminController::class.java)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/artists", params = ["name"])
    fun getArtistsByName(@RequestParam(name = "name") name: String): List<AdminArtistDto> {
        return artistService.findByName(name).map { convertToDto(it) }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/artists/{id}", params = ["name"])
    fun updateArtistName(@PathVariable id: Long, @RequestParam(name = "name") name: String) {
        val artistOptional = artistRepository.findById(id)
        if (artistOptional.isPresent) {
            val artist = artistOptional.get()
            artist.name = name
            artistRepository.save(artist)
        } else {
            log.warn("Artist with id $id not found")
        }
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/artists/{id}")
    fun deleteArtistById(@PathVariable id: Long) {
        artistRepository.deleteById(id)
        log.info("artist $id deleted")
    }


    private fun convertToDto(artist: Artist): AdminArtistDto {
        return AdminArtistDto(id = artist.id,
            name = artist.name,
            background = artist.background,
            wikimediaPhotos = artist.photos.map { convertToDto(it) }.toSet(),
            flickrPhotos = emptySet(),
            logEntries = artist.logEntries.map { convertToDto(it) })
    }

    private fun convertToDto(wikimediaPhoto: ArtistPhoto): AdminArtistWikimediaPhotoDto {
        return AdminArtistWikimediaPhotoDto(
            url = wikimediaPhoto.url.toString(), attribution = wikimediaPhoto.attribution
        )
    }

    private fun convertToDto(logEntry: ArtistLogEntry): AdminArtistLogEntryDto {
        return AdminArtistLogEntryDto(
            date = logEntry.date, username = logEntry.username
        )
    }
}
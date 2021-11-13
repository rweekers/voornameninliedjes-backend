package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistFlickrPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistLogEntryDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.AdminArtistWikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class ArtistAdminController {

    private val log = LoggerFactory.getLogger(ArtistAdminController::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var artistService: ArtistService

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/artists", params = ["name"])
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun getArtistsByName(@RequestParam(name = "name") name: String): List<AdminArtistDto> {
        return artistService.findByName(name)
            .map { convertToDto(it) }
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/artists/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://beheer.voornameninliedjes.nl"])
    fun deleteArtistById(@PathVariable id: Long) {
        artistRepository.deleteById(id)
        log.info("artist $id deleted")
    }


    private fun convertToDto(artist: Artist): AdminArtistDto {
        return AdminArtistDto(
            id = artist.id,
            name = artist.name,
            background = artist.background,
            wikimediaPhotos = artist.wikimediaPhotos.map { convertToDto(it) }.toSet(),
            flickrPhotos = artist.flickrPhotos.map { convertToDto(it) }.toSet(),
            logEntries = artist.logEntries.map { convertToDto(it) }
        )
    }

    private fun convertToDto(wikimediaPhoto: ArtistWikimediaPhoto): AdminArtistWikimediaPhotoDto {
        return AdminArtistWikimediaPhotoDto(
            url = wikimediaPhoto.url,
            attribution = wikimediaPhoto.attribution
        )
    }

    private fun convertToDto(flickrPhoto: ArtistFlickrPhoto): AdminArtistFlickrPhotoDto {
        return AdminArtistFlickrPhotoDto(
            flickrId = flickrPhoto.flickrId
        )
    }

    private fun convertToDto(logEntry: ArtistLogEntry): AdminArtistLogEntryDto {
        return AdminArtistLogEntryDto(
            date = logEntry.date,
            username = logEntry.username
        )
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistFlickrPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistWikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
class ArtistController {

    private val log = LoggerFactory.getLogger(ArtistController::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @GetMapping("/artists/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getArtistById(@PathVariable id: Long): Optional<ArtistDto> {
        log.info("Requesting artist with id $id...")
        return artistRepository.findById(id).map { convertToDto(it) }
    }

    @GetMapping("/artists", params = ["name"])
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getArtistsByName(@RequestParam(name = "name") name: String): List<ArtistDto> {
        return artistRepository.findByNameIgnoreCase(name).map { convertToDto(it) }
    }

    @GetMapping("/artists")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getArtists(): List<ArtistDto> {
        log.info("Requesting all artists...")
        return artistRepository.findAllOrderedByName().map { convertToDto(it) }
    }

    private fun convertToDto(artist: Artist): ArtistDto {
        return ArtistDto(
            id = artist.id,
            name = artist.name,
            background = artist.background,
            wikimediaPhotos = artist.wikimediaPhotos.map { convertToDto(it) }.toSet(),
            flickrPhotos = artist.flickrPhotos.map { convertToDto(it) }.toSet(),
        )
    }

    private fun convertToDto(wikimediaPhoto: ArtistWikimediaPhoto): ArtistWikimediaPhotoDto {
        return ArtistWikimediaPhotoDto(
            url = wikimediaPhoto.url,
            attribution = wikimediaPhoto.attribution
        )
    }

    private fun convertToDto(flickrPhoto: ArtistFlickrPhoto): ArtistFlickrPhotoDto {
        return ArtistFlickrPhotoDto(
            flickrId = flickrPhoto.flickrId
        )
    }
}
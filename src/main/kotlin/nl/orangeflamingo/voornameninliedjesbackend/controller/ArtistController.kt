package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistWikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RestController
@RequestMapping("/api", produces = ["application/vnd.voornameninliedjes.artists.v1+json"])
class ArtistController(private val artistRepository: ArtistRepository) {

    private val log = LoggerFactory.getLogger(ArtistController::class.java)

    @GetMapping("/artists/{id}")
    fun getArtistById(@PathVariable id: Long): Optional<ArtistDto> {
        log.info("Requesting artist with id $id...")
        return artistRepository.findById(id).map { convertToDto(it) }
    }

    @GetMapping(value = ["/artists", "/artists/"])
    fun getArtists(): List<ArtistDto> {
        log.info("Requesting all artists...")
        return artistRepository.findAllOrderedByName().map { convertToDto(it) }
    }

    private fun convertToDto(artist: Artist): ArtistDto {
        return ArtistDto(
            id = artist.id,
            name = artist.name,
            background = artist.background,
            wikimediaPhotos = artist.photos.map { convertToDto(it) }.toSet(),
            flickrPhotos = emptySet(),
        )
    }

    private fun convertToDto(wikimediaPhoto: ArtistPhoto): ArtistWikimediaPhotoDto {
        return ArtistWikimediaPhotoDto(
            url = wikimediaPhoto.url,
            attribution = wikimediaPhoto.attribution
        )
    }
}
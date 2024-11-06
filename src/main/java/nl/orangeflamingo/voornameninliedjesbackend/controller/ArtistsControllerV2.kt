package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.api.ArtistsApi
import nl.orangeflamingo.voornameninliedjesbackend.model.Artist
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

    private fun toArtistMessage(artist: nl.orangeflamingo.voornameninliedjesbackend.domain.Artist): Artist {
        return Artist.builder()
            .name(artist.name)
            .imageUrl(URI.create(artist.wikimediaPhotos.firstOrNull()?.url ?: ""))
            .build()
    }

}
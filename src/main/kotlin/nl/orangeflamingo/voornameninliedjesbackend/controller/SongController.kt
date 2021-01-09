package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.PhotoDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.dto.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.SongService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/beta")
class SongController {

    private val log = LoggerFactory.getLogger(SongController::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var songRepository: SongRepository

    @Autowired
    private lateinit var songService: SongService

    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    fun getSongById(@PathVariable id: Long): SongDto {
        log.info("Requesting song with id $id...")
        return convertToDto(songService.findById(id))
    }

    @GetMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Summary::class)
    fun getSongs(): List<SongDto> {
        log.info("Requesting all songs...")
        return songRepository.findAllOrderedByName().map { convertToDto(Pair(it, emptySet())) }
    }

    private fun convertToDto(entry: Pair<Song, Set<PhotoDetail>>): SongDto {
        val song = entry.first
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()

        return SongDto(
            id = song.id.toString(),
            artist = artist.name,
            title = song.title,
            name = song.name,
            artistImage = song.artistImage,
            background = song.background,
            youtube = song.youtube,
            spotify = song.spotify,
            wikimediaPhotos = artist.wikimediaPhotos.map { convertWikimediaPhotoToDto(it) }.toSet(),
            flickrPhotos = entry.second.map {
                PhotoDto(
                    id = it.id,
                    url = it.url,
                    farm = it.farm,
                    secret = it.secret,
                    server = it.server,
                    title = it.title,
                    owner = FlickrOwnerDto(
                        id = it.ownerDetail?.id ?: "",
                        username = it.ownerDetail?.username ?: "",
                        photoUrl = it.ownerDetail?.photosUrl ?: ""
                    ),
                    license = FlickrLicenseDto(
                        name = it.licenseDetail?.name ?: "",
                        url = it.licenseDetail?.url ?: ""
                    )
                )
            }.toSet(),
            sources = song.sources.map {
                SourceDto(
                    url = it.url,
                    name = it.name
                )
            }.toSet(),
            status = song.status.name
        )
    }

    private fun convertWikimediaPhotoToDto(wikimediaPhoto: ArtistWikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }
}
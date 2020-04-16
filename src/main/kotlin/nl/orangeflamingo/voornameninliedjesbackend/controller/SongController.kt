package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.SongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.WikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.SongRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class SongController {

    @Autowired
    private lateinit var songRepository: SongRepository

    @GetMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    @JsonView(Views.Summary::class)
    fun getSongs(): Flux<SongDto> {
        return songRepository.findAllByStatusOrderByName(SongStatus.SHOW).map { convertToDto(it) }.flatMap { enrichSong(it) }
    }

    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Detail::class)
    fun getSongById(@PathVariable("id") id: String): Mono<SongDto> {
        return songRepository.findById(id).map { convertToDto(it) }.flatMap { enrichSong(it) }
    }

    private fun convertToDto(song: Song): SongDto {
        return SongDto(
                id = song.id,
                artist = song.artist,
                title = song.title,
                name = song.name,
                artistImage = "https://ak9.picdn.net/shutterstock/videos/24149239/thumb/1.jpg",
                background = song.background,
                youtube = song.youtube,
                spotify = song.spotify,
                wikimediaPhotos = song.wikimediaPhotos.map { wikimediaPhoto -> convertWikimediaPhotoToDto(wikimediaPhoto) }.toSet(),
                flickrPhotos = song.flickrPhotos,
                status = song.status?.name)
    }

    private fun convertWikimediaPhotoToDto(wikimediaPhoto: WikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }

    private fun enrichSong(song: SongDto): Mono<SongDto> {
        val wikimediaPhoto = song.wikimediaPhotos.firstOrNull()
        if (wikimediaPhoto != null) {
            return Mono.just(song.copy(artistImage = wikimediaPhoto.url))
        }
        val flickrPhotoId = song.flickrPhotos.firstOrNull()
        if (flickrPhotoId != null) {
            val webClient = WebClient.builder()
                    .baseUrl("https://api.flickr.com")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()

            val photo = webClient.get().uri("/services/rest/?method=flickr.photos.getInfo&api_key=9676a28e9cb321d2721e813055abb6dc&format=json&nojsoncallback=true&photo_id={flickr_photo_id}", flickrPhotoId)
                    .retrieve()
                    .bodyToMono(FlickrPhotoDto::class.java)
                    .onErrorResume { Mono.empty() }
                    .block()
                    ?.photo

            if (photo != null) {
                return Mono.just(song.copy(artistImage = "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}_c.jpg"))
            }
        }
        return Mono.just(song)
    }
}

package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.dto.SongDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.SongRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class SongController {

    @Autowired
    private lateinit var songRepository: SongRepository

    @GetMapping("/songs")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Summary::class)
    fun getSongs(): Flux<SongDto> {
        return songRepository.findAllByStatus(SongStatus.SHOW).map { convertToDto(it) }
    }

    @GetMapping("/songs/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl"])
    @JsonView(Views.Detail::class)
    fun getSongById(@PathVariable("id") id: String): Mono<SongDto> {
        return songRepository.findById(id).map { convertToDto(it) }
    }

    private fun convertToDto(song: Song): SongDto {
        return SongDto(song.id, song.artist, song.title, song.name, song.background, song.youtube, song.spotify, song.flickrPhotos, song.status.name)
    }
}

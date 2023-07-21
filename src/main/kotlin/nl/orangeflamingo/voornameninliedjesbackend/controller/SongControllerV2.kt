package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.dto.SongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.SourceDto
import nl.orangeflamingo.voornameninliedjesbackend.service.SongServiceV2
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SongControllerV2(
    private val songServiceV2: SongServiceV2
) {

    @GetMapping("/songs", params = ["name-starts-with", "page-number"])
    fun getSongsWithNameStartingWith(
        @RequestParam(name = "name-starts-with") nameStartsWith: String,
        @RequestParam(name = "page-number") pageNumber: String
    ): Slice<SongDto> {
        return songServiceV2.findByNameStartingWith(nameStartsWith, SongStatus.SHOW, Pageable.ofSize(30).withPage(pageNumber.toInt()))
            .map { convertToDto(it.first, it.second) }
    }

    private fun convertToDto(song: Song, artist: Artist): SongDto {
        return SongDto(
            artist = artist.name,
            title = song.title,
            name = song.name,
            artistImage = song.artistImage,
            artistImageAttribution = song.artistImageAttribution,
            localImage = song.localImage,
            blurredImage = song.blurredImage,
            artistImageWidth = song.artistImageWidth,
            artistImageHeight = song.artistImageHeight,
            artistLastFmUrl = artist.lastFmUrl,
            wikipediaPage = song.wikipediaPage,
            youtube = song.youtube,
            lastFmUrl = song.lastFmUrl,
            albumName = song.albumName,
            albumLastFmUrl = song.albumLastFmUrl,
            spotify = song.spotify,
            background = song.background,
            wikipediaNl = null,
            wikipediaEn = null,
            wikipediaSummaryEn = null,
            wikimediaPhotos = emptySet(),
            flickrPhotos = emptySet(),
            sources = song.sources.map {
                SourceDto(
                    url = it.url,
                    name = it.name
                )
            }.toSet(),
            tags = emptySet(),
            hasDetails = song.hasDetails
        )
    }
}
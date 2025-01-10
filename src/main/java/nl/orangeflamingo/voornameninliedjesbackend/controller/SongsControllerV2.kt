package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.api.SongsApi
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWithArtist
import nl.orangeflamingo.voornameninliedjesbackend.model.Song
import nl.orangeflamingo.voornameninliedjesbackend.service.SongServiceV2
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SongsControllerV2(private val songServiceV2: SongServiceV2) : SongsApi {

    private val log = LoggerFactory.getLogger(SongsControllerV2::class.java)

    override fun getApiSongs(nameStartsWith: String?, offset: Int, limit: Int): ResponseEntity<List<Song>> {
        log.info("Requesting all songs v2 with first characters {}...", nameStartsWith)
        return ResponseEntity.ok(songServiceV2.findByNameStartingWith(
            nameStartsWith,
            SongStatus.SHOW,
            Pageable.ofSize(limit).withPage(offset)
        ).map { convertToDto(it) })
    }

    private fun convertToDto(songWithArtist: SongWithArtist): Song {
        return Song.builder()
            .name(songWithArtist.name)
            .title(songWithArtist.title)
            .artist(songWithArtist.artist)
            .hasDetails(songWithArtist.hasDetails)
            .artistImage(songWithArtist.artistImage)
            .artistImageAttribution(songWithArtist.artistImageAttribution)
            .localImage(songWithArtist.localImage)
            .artistImageWidth(songWithArtist.artistImageWidth)
            .artistImageHeight(songWithArtist.artistImageHeight)
            .build()
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.api.SongsApi
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWithArtist
import nl.orangeflamingo.voornameninliedjesbackend.domain.Photo
import nl.orangeflamingo.voornameninliedjesbackend.model.Song
import nl.orangeflamingo.voornameninliedjesbackend.model.SongDetail
import nl.orangeflamingo.voornameninliedjesbackend.service.NotFoundException
import nl.orangeflamingo.voornameninliedjesbackend.service.SongServiceV2
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

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
            .artistImage(if (songWithArtist.artistImage != null) URI.create(songWithArtist.artistImage) else null)
            .artistImageAttribution(songWithArtist.artistImageAttribution)
            .localImage(songWithArtist.localImage)
            .artistImageWidth(songWithArtist.artistImageWidth)
            .artistImageHeight(songWithArtist.artistImageHeight)
            .build()
    }

    override fun getApiSongDetail(artist: String, title: String): ResponseEntity<SongDetail> {
        log.info("Get song details for {} - {}", artist, title)
        try {
            return ResponseEntity.ok(convertToDto(songServiceV2.findByArtistAndTitle(artist, title)))
        } catch (ex: NotFoundException) {
            log.warn("Could not get song details for {} - {}", artist, title)
            return ResponseEntity.notFound().build()
        }
    }

    private fun convertToDto(songDetail: nl.orangeflamingo.voornameninliedjesbackend.domain.SongDetail): SongDetail {
        return SongDetail.builder()
            .name(songDetail.name)
            .title(songDetail.title)
            .artist(songDetail.artist)
            .hasDetails(songDetail.hasDetails)
            .youtube(songDetail.youtube)
            .spotify(songDetail.spotify)
            .background(songDetail.background)
            .localImage(songDetail.localImage)
            .artistImageWidth(songDetail.artistImageWidth)
            .artistImageHeight(songDetail.artistImageHeight)
            .wikimediaPhotos(songDetail.photos.map { convert(it) })
            .sources(emptyList())
            .build()
    }

    private fun convert(photo: Photo): nl.orangeflamingo.voornameninliedjesbackend.model.WikimediaPhoto {
        return nl.orangeflamingo.voornameninliedjesbackend.model.WikimediaPhoto(

        ).url(URI.create(photo.url)).attribution(photo.attribution)
    }
}
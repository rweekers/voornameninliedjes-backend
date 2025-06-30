package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.api.SongsApi
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmAlbum
import nl.orangeflamingo.voornameninliedjesbackend.domain.Photo
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongSource
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWithArtist
import nl.orangeflamingo.voornameninliedjesbackend.model.LastFmAlbumDto
import nl.orangeflamingo.voornameninliedjesbackend.model.SongDetailDto
import nl.orangeflamingo.voornameninliedjesbackend.model.SongDto
import nl.orangeflamingo.voornameninliedjesbackend.model.SongPageDto
import nl.orangeflamingo.voornameninliedjesbackend.model.SourceDto
import nl.orangeflamingo.voornameninliedjesbackend.model.TagDto
import nl.orangeflamingo.voornameninliedjesbackend.service.NotFoundException
import nl.orangeflamingo.voornameninliedjesbackend.service.SongServiceV2
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class SongsControllerV2(private val songServiceV2: SongServiceV2) : SongsApi {

    private val log = LoggerFactory.getLogger(SongsControllerV2::class.java)

    override fun getApiSongs(nameStartsWith: String?, offset: Int, limit: Int): ResponseEntity<SongPageDto> {
        log.info("Requesting all songs v2 with first characters {} ({}/{})...", nameStartsWith, offset, limit)
        return ResponseEntity.ok(
            songServiceV2.findByNameStartingWith(
                nameStartsWith,
                SongStatus.SHOW,
                Pageable.ofSize(limit).withPage(offset)
            )
                .let { SongPageDto(it.songs.map { s -> convertToDto(s) }, it.totalItems, it.isLastPage) }
        )
    }

    private fun convertToDto(songWithArtist: SongWithArtist): SongDto {
        return SongDto(
            name = songWithArtist.name,
            title = songWithArtist.title,
            artist = songWithArtist.artist,
            hasDetails = songWithArtist.hasDetails,
            artistImage = if (songWithArtist.artistImage != null) URI.create(songWithArtist.artistImage) else null,
            artistImageAttribution = songWithArtist.artistImageAttribution,
            localImage = songWithArtist.localImage,
            artistImageWidth = songWithArtist.artistImageWidth,
            artistImageHeight = songWithArtist.artistImageHeight
        )
    }

    override fun getApiSongDetail(artist: String, title: String): ResponseEntity<SongDetailDto> {
        log.info("Get song details for {} - {}", artist, title)
        try {
            return ResponseEntity.ok(convertToDto(songServiceV2.findByArtistAndTitle(artist, title)))
        } catch (_: NotFoundException) {
            log.warn("Could not get song details for {} - {}", artist, title)
            return ResponseEntity.notFound().build()
        }
    }

    private fun convertToDto(songDetail: SongDetail): SongDetailDto {
        return SongDetailDto(
            name = songDetail.name,
            title = songDetail.title,
            artist = songDetail.artist,
            hasDetails = songDetail.hasDetails,
            youtube = songDetail.youtube,
            spotify = songDetail.spotify,
            background = songDetail.background,
            localImage = songDetail.localImage,
            blurredImage = songDetail.blurredImage,
            artistImageWidth = songDetail.artistImageWidth,
            artistImageHeight = songDetail.artistImageHeight,
            wikipediaPage = songDetail.wikipediaPage,
            wikiContentNl = songDetail.wikiContentNl,
            wikiContentEn = songDetail.wikiContentEn,
            wikiSummaryEn = songDetail.wikiSummaryEn,
            lastFmAlbum = convert(songDetail.lastfmAlbum),
            photos = songDetail.photos.map { convert(it) },
            sources = songDetail.sources.map { convert(it) },
            tags = songDetail.tags.map { convert(it) }
        )
    }

    private fun convert(photo: Photo): nl.orangeflamingo.voornameninliedjesbackend.model.PhotoDto {
        return nl.orangeflamingo.voornameninliedjesbackend.model.PhotoDto(
            url = URI.create(photo.url),
            attribution = photo.attribution,
        )
    }

    private fun convert(lastFmAlbum: LastFmAlbum?): LastFmAlbumDto? {
        if (lastFmAlbum == null) {
            return null
        }
        return LastFmAlbumDto(
            name = lastFmAlbum.name,
            url = URI.create(lastFmAlbum.url),
            mbid = if (lastFmAlbum.mbid != null) UUID.fromString(lastFmAlbum.mbid) else null
        )
    }

    private fun convert(source: SongSource): SourceDto {
        return SourceDto(URI.create(source.url), source.name)
    }

    private fun convert(tag: SongLastFmTag): TagDto {
        return TagDto(URI.create(tag.url), tag.name)
    }
}
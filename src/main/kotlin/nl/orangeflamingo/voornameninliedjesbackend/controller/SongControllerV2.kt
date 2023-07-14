package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.AggregateSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.PhotoDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrLicenseDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrOwnerDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.PhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.SongDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.SourceDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.WikimediaPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepositoryV2
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SongControllerV2(
    private val songRepositoryV2: SongRepositoryV2
) {

    @GetMapping("/songs", params = ["first-character", "page-size", "page-number"])
    fun getSongsWithNameStartingWith(
        @RequestParam(name = "first-character") firstCharacter: String,
        @RequestParam(name = "page-size") pageSize: String,
        @RequestParam(name = "page-number") pageNumber: String
    ): Page<Song> {
        return songRepositoryV2.findByNameStartingWithAndStatusOrderByName(firstCharacter, SongStatus.SHOW, Pageable.ofSize(pageSize.toInt()).withPage(pageNumber.toInt()))
            //.map { convertToDto(it, emptyList()) }
    }

    private fun convertToDto(song: AggregateSong, photos: List<PhotoDetail>): SongDto {
        return SongDto(
            artist = song.artistName,
            title = song.title,
            name = song.name,
            artistImage = song.artistImage,
            artistImageAttribution = song.artistImageAttribution,
            localImage = song.localImage,
            blurredImage = song.blurredImage,
            artistImageWidth = song.artistImageWidth,
            artistImageHeight = song.artistImageHeight,
            artistLastFmUrl = song.artistLastFmUrl,
            wikipediaPage = song.wikipediaPage,
            youtube = song.youtube,
            lastFmUrl = song.lastFmUrl,
            albumName = song.albumName,
            albumLastFmUrl = song.albumLastFmUrl,
            spotify = song.spotify,
            background = song.background,
            wikipediaNl = song.wikipediaContentNl,
            wikipediaEn = song.wikipediaContentEn,
            wikipediaSummaryEn = song.wikipediaSummaryEn,
            wikimediaPhotos = mergeAndConvertWikimediaPhotos(song.artistWikimediaPhotos, song.songWikimediaPhotos),
            flickrPhotos = photos.map {
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
            tags = song.tags.map {
                LastFmTagDto(
                    name = it.name,
                    url = it.url
                )
            }.toSet(),
            hasDetails = song.hasDetails
        )
    }

    private fun mergeAndConvertWikimediaPhotos(
        artistWikimediaPhoto: Set<ArtistWikimediaPhoto>,
        songWikimediaPhotos: Set<SongWikimediaPhoto>
    ): Set<WikimediaPhotoDto> {
        val mergedPhotos = mutableSetOf<WikimediaPhotoDto>()
        mergedPhotos.addAll(songWikimediaPhotos.map { convertSongWikimediaPhotoToDto(it) })
        mergedPhotos.addAll(artistWikimediaPhoto.map { convertArtistWikimediaPhotoToDto(it) })
        return mergedPhotos
    }

    private fun convertArtistWikimediaPhotoToDto(wikimediaPhoto: ArtistWikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }

    private fun convertSongWikimediaPhotoToDto(wikimediaPhoto: SongWikimediaPhoto): WikimediaPhotoDto {
        return WikimediaPhotoDto(wikimediaPhoto.url, wikimediaPhoto.attribution)
    }
}
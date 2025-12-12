package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.controller.Views
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class SongDto(
    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val artist: String,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val title: String,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val name: String,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val artistImage: String?,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val localImage: String?,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val blurredImage: String?,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val artistImageWidth: Int?,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val artistImageHeight: Int?,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val artistImageAttribution: String?,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val hasDetails: Boolean,

    @param:JsonView(Views.Detail::class)
    val artistLastFmUrl: String?,

    @param:JsonView(Views.Detail::class)
    val wikipediaPage: String?,

    @param:JsonView(Views.Detail::class)
    val youtube: String?,

    @param:JsonView(Views.Detail::class)
    val background: String?,

    @param:JsonView(Views.Detail::class)
    val wikipediaNl: String?,

    @param:JsonView(Views.Detail::class)
    val wikipediaEn: String?,

    @param:JsonView(Views.Detail::class)
    val wikipediaSummaryEn: String?,

    @param:JsonView(Views.Detail::class)
    val lastFmUrl: String?,

    @param:JsonView(Views.Detail::class)
    val albumName: String?,

    @param:JsonView(Views.Detail::class)
    val albumLastFmUrl: String?,

    @param:JsonView(Views.Summary::class, Views.Detail::class)
    val spotify: String?,

    @param:JsonView(Views.Detail::class)
    val wikimediaPhotos: Set<WikimediaPhotoDto>,

    @param:JsonView(Views.Detail::class)
    val flickrPhotos: Set<PhotoDto>,

    @param:JsonView(Views.Detail::class)
    val sources: Set<SourceDto>,

    @param:JsonView(Views.Detail::class)
    val tags: Set<LastFmTagDto>,
)

data class WikimediaPhotoDto(
    @param:JsonView(Views.Detail::class)
    val url: String,
    @param:JsonView(Views.Detail::class)
    val attribution: String
)

data class FlickrOwnerDto(
    @param:JsonView(Views.Detail::class)
    val id: String,
    @param:JsonView(Views.Detail::class)
    val username: String,
    @param:JsonView(Views.Detail::class)
    val photoUrl: String
)

data class FlickrLicenseDto(
    @param:JsonView(Views.Detail::class)
    val name: String,
    @param:JsonView(Views.Detail::class)
    val url: String
)

data class PhotoDto(
    @param:JsonView(Views.Detail::class)
    val url: String,
    @param:JsonView(Views.Detail::class)
    val farm: String,
    @param:JsonView(Views.Detail::class)
    val server: String,
    @param:JsonView(Views.Detail::class)
    val id: String,
    @param:JsonView(Views.Detail::class)
    val secret: String,
    @param:JsonView(Views.Detail::class)
    val title: String,
    @param:JsonView(Views.Detail::class)
    val owner: FlickrOwnerDto,
    @param:JsonView(Views.Detail::class)
    val license: FlickrLicenseDto
)

data class SourceDto(
    @param:JsonView(Views.Detail::class)
    val url: String,
    @param:JsonView(Views.Detail::class)
    val name: String
)

package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.controller.Views
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class SongDto(
    @JsonView(Views.Summary::class, Views.Detail::class)
    val artist: String,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val title: String,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val name: String,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val artistImage: String?,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val localImage: String?,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val artistImageWidth: Int?,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val artistImageHeight: Int?,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val artistImageAttribution: String?,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val hasDetails: Boolean,

    @JsonView(Views.Detail::class)
    val artistLastFmUrl: String?,

    @JsonView(Views.Detail::class)
    val wikipediaPage: String?,

    @JsonView(Views.Detail::class)
    val youtube: String?,

    @JsonView(Views.Detail::class)
    val background: String?,

    @JsonView(Views.Detail::class)
    val wikipediaNl: String?,

    @JsonView(Views.Detail::class)
    val wikipediaEn: String?,

    @JsonView(Views.Detail::class)
    val wikipediaSummaryEn: String?,

    @JsonView(Views.Detail::class)
    val lastFmUrl: String?,

    @JsonView(Views.Detail::class)
    val albumName: String?,

    @JsonView(Views.Detail::class)
    val albumLastFmUrl: String?,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val spotify: String?,

    @JsonView(Views.Detail::class)
    val wikimediaPhotos: Set<WikimediaPhotoDto>,

    @JsonView(Views.Detail::class)
    val flickrPhotos: Set<PhotoDto>,

    @JsonView(Views.Detail::class)
    val sources: Set<SourceDto>,

    @JsonView(Views.Detail::class)
    val tags: Set<LastFmTagDto>,
)

data class WikimediaPhotoDto(
    @JsonView(Views.Detail::class)
    val url: String,
    @JsonView(Views.Detail::class)
    val attribution: String
)

data class FlickrOwnerDto(
    @JsonView(Views.Detail::class)
    val id: String,
    @JsonView(Views.Detail::class)
    val username: String,
    @JsonView(Views.Detail::class)
    val photoUrl: String
)

data class FlickrLicenseDto(
    @JsonView(Views.Detail::class)
    val name: String,
    @JsonView(Views.Detail::class)
    val url: String
)

data class PhotoDto(
    @JsonView(Views.Detail::class)
    val url: String,
    @JsonView(Views.Detail::class)
    val farm: String,
    @JsonView(Views.Detail::class)
    val server: String,
    @JsonView(Views.Detail::class)
    val id: String,
    @JsonView(Views.Detail::class)
    val secret: String,
    @JsonView(Views.Detail::class)
    val title: String,
    @JsonView(Views.Detail::class)
    val owner: FlickrOwnerDto,
    @JsonView(Views.Detail::class)
    val license: FlickrLicenseDto
)

data class SourceDto(
    @JsonView(Views.Detail::class)
    val url: String,
    @JsonView(Views.Detail::class)
    val name: String
)

package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.controller.Views

data class SongDto(
    @JsonView(Views.Summary::class, Views.Detail::class)
    val id: String?,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val artist: String,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val title: String,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val name: String,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val artistImage: String?,

    @JsonView(Views.Summary::class, Views.Detail::class)
    val hasDetails: Boolean,

    @JsonView(Views.Detail::class)
    val background: String?,

    @JsonView(Views.Detail::class)
    val wikipediaPage: String?,

    @JsonView(Views.Detail::class)
    val youtube: String?,

    @JsonView(Views.Detail::class)
    val spotify: String?,

    @JsonView(Views.Detail::class)
    val wikimediaPhotos: Set<WikimediaPhotoDto>,

    @JsonView(Views.Detail::class)
    val flickrPhotos: Set<PhotoDto>,

    @JsonView(Views.Detail::class)
    val sources: Set<SourceDto>,

    @JsonView(Views.Detail::class)
    val status: String
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

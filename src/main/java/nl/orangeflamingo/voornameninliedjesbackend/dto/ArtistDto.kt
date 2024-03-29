package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class ArtistDto(
    val id: Long?,
    val name: String,
    val background: String?,
    var wikimediaPhotos: Set<ArtistWikimediaPhotoDto> = setOf(),
    var flickrPhotos: Set<ArtistFlickrPhotoDto> = setOf()
)

data class ArtistWikimediaPhotoDto(
    val url: String,
    val attribution: String
)

data class ArtistFlickrPhotoDto(
    val flickrId: String
)
package nl.orangeflamingo.voornameninliedjesbackend.dto

import java.time.Instant

data class AdminArtistDto(
    val id: Long?,
    val name: String,
    val background: String?,
    var wikimediaPhotos: Set<AdminArtistWikimediaPhotoDto> = setOf(),
    var flickrPhotos: Set<AdminArtistFlickrPhotoDto> = setOf(),
    val logEntries: List<AdminArtistLogEntryDto> = listOf()
)

data class AdminArtistWikimediaPhotoDto(
    val url: String,
    val attribution: String
)

data class AdminArtistFlickrPhotoDto(
    val flickrId: String
)

class AdminArtistLogEntryDto(
    val date: Instant,
    val username: String
)
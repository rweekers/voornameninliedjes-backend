package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class AdminArtistDto @JsonCreator constructor(
    @param:JsonProperty("id") val id: Long?,
    @param:JsonProperty("name") val name: String,
    @param:JsonProperty("background") val background: String?,
    @param:JsonProperty("wikimediaPhotos") var wikimediaPhotos: Set<AdminArtistWikimediaPhotoDto> = setOf(),
    @param:JsonProperty("flickrPhotos") var flickrPhotos: Set<AdminArtistFlickrPhotoDto> = setOf(),
    @param:JsonProperty("logEntries") val logEntries: List<AdminArtistLogEntryDto> = listOf()
)

data class AdminArtistWikimediaPhotoDto @JsonCreator constructor(
    @param:JsonProperty("url") val url: String,
    @param:JsonProperty("attribution") val attribution: String
)

data class AdminArtistFlickrPhotoDto(
    val flickrId: String
)

class AdminArtistLogEntryDto @JsonCreator constructor(
    @param:JsonProperty("date") val date: Instant,
    @param:JsonProperty("username") val username: String
)
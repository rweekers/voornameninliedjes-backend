package nl.orangeflamingo.voornameninliedjesbackend.dto


import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class AdminSongDto @JsonCreator constructor(
    @param:JsonProperty("id") val id: String? = null,
    @param:JsonProperty("artist") val artist: String,
    @param:JsonProperty("title") val title: String,
    @param:JsonProperty("name") val name: String,
    @param:JsonProperty("artistImage") val artistImage: String? = null,
    @param:JsonProperty("localImage") val localImage: String? = null,
    @param:JsonProperty("blurredImage") val blurredImage: String? = null,
    @param:JsonProperty("artistLastFmUrl") val artistLastFmUrl: String? = null,
    @param:JsonProperty("background") val background: String? = null,
    @param:JsonProperty("wikipediaPage") val wikipediaPage: String? = null,
    @param:JsonProperty("youtube") val youtube: String? = null,
    @param:JsonProperty("spotify") val spotify: String? = null,
    @param:JsonProperty("wikipediaNl") val wikipediaNl: String? = null,
    @param:JsonProperty("wikipediaEn") val wikipediaEn: String? = null,
    @param:JsonProperty("wikipediaSummaryEn") val wikipediaSummaryEn: String? = null,
    @param:JsonProperty("lastFmUrl") val lastFmUrl: String? = null,
    @param:JsonProperty("albumName") val albumName: String? = null,
    @param:JsonProperty("albumLastFmUrl") val albumLastFmUrl: String? = null,
    @param:JsonProperty("status") val status: String,
    @param:JsonProperty("remarks") val remarks: String? = null,
    @param:JsonProperty("hasDetails") val hasDetails: Boolean = false,
    @param:JsonProperty("artistWikimediaPhotos") val artistWikimediaPhotos: List<AdminWikimediaPhotoDto> = listOf(),
    @param:JsonProperty("songWikimediaPhotos") val songWikimediaPhotos: List<AdminWikimediaPhotoDto> = listOf(),
    @param:JsonProperty("sources") val sources: List<AdminSourceDto> = listOf(),
    @param:JsonProperty("tags") val tags: List<LastFmTagDto> = listOf(),
    @param:JsonProperty("logs") val logs: List<AdminLogEntry> = listOf()
)

data class AdminWikimediaPhotoDto @JsonCreator constructor(
    @param:JsonProperty("url")val url: String,
    @param:JsonProperty("attribution")val attribution: String
)

data class AdminSourceDto @JsonCreator constructor(
    @param:JsonProperty("url")val url: String,
    @param:JsonProperty("name")val name: String
)

data class AdminLogEntry @JsonCreator constructor(
    @param:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Europe/Amsterdam")
    @param:JsonProperty("date")val date: Instant = Instant.now(),
    @param:JsonProperty("user")val user: String
)
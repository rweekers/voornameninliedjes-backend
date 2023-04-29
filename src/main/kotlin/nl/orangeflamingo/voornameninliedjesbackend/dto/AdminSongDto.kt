package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.*
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class AdminSongDto(

    val id: String? = null,

    val artist: String,

    val title: String,

    val name: String,

    val artistImage: String? = null,

    val localImage: String? = null,

    val blurredImage: String? = null,

    val artistLastFmUrl: String? = null,

    val background: String? = null,

    val wikipediaPage: String? = null,

    val youtube: String? = null,

    val spotify: String? = null,

    val wikipediaNl: String? = null,

    val wikipediaEn: String? = null,

    val wikipediaSummaryEn: String? = null,

    val lastFmUrl: String? = null,

    val albumName: String? = null,

    val albumLastFmUrl: String? = null,

    val status: String,

    val remarks: String? = null,

    val hasDetails: Boolean = false,

    val artistWikimediaPhotos: List<AdminWikimediaPhotoDto> = listOf(),

    val songWikimediaPhotos: List<AdminWikimediaPhotoDto> = listOf(),

    val flickrPhotos: List<String> = listOf(),

    val sources: List<AdminSourceDto> = listOf(),

    val tags: List<LastFmTagDto> = listOf(),

    @JsonIgnoreProperties(allowGetters = true)
    val logs: List<AdminLogEntry> = listOf()
)

data class AdminWikimediaPhotoDto(
    val url: String,
    val attribution: String
)

data class AdminSourceDto(
    val url: String,
    val name: String
)

data class AdminLogEntry(

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Europe/Amsterdam")
    val date: Instant = Instant.now(),
    val user: String
)
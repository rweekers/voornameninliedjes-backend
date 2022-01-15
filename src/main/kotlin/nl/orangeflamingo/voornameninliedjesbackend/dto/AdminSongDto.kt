package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class AdminSongDto(

    val id: String? = null,

    val artist: String,

    val title: String,

    val name: String,

    val artistImage: String? = null,

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

    val artistWikimediaPhotos: Set<AdminWikimediaPhotoDto> = setOf(),

    val songWikimediaPhotos: Set<AdminWikimediaPhotoDto> = setOf(),

    val flickrPhotos: Set<String> = setOf(),

    val sources: Set<AdminSourceDto> = setOf(),

    val tags: Set<LastFmTagDto> = setOf(),

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
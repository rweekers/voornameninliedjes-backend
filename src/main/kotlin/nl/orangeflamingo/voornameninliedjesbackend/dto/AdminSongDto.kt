package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant

data class AdminSongDto(

    val id: String? = null,

    val artist: String,

    val title: String,

    val name: String,

    val artistImage: String? = null,

    val background: String? = null,

    val wikipediaPage: String? = null,

    val youtube: String? = null,

    val spotify: String? = null,

    val status: String,

    val wikimediaPhotos: Set<AdminWikimediaPhotoDto> = setOf(),

    val flickrPhotos: Set<String> = setOf(),

    val sources: Set<AdminSourceDto> = setOf(),

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
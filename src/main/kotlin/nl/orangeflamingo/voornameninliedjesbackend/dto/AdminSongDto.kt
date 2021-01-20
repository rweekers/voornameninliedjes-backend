package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant
import java.util.*

data class AdminSongDto(

        val id: String?,

        val artist: String,

        val title: String,

        val name: String,

        val artistImage: String?,

        val background: String?,

        val youtube: String?,

        val spotify: String?,

        val status: String,

        val wikimediaPhotos: Set<AdminWikimediaPhotoDto> = setOf(),

        val flickrPhotos: Set<String> = setOf(),

        val sources: Set<AdminSourceDto> = setOf(),

        @JsonIgnoreProperties(allowGetters = true)
        val logs: List<AdminLogEntry> = listOf()

) {
    override fun toString(): String {
        return "Song(name=$artist, code=$title"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is AdminSongDto)
            return false
        return artist == other.artist && title == other.title && name == other.name
    }

    override fun hashCode(): Int {
        return Objects.hash(artist, title, name)
    }
}

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
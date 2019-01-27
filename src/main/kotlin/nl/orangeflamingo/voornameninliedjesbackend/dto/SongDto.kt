package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant
import java.util.*
import javax.annotation.Generated

@CompoundIndex(name = "song_uq_idx", def = "{'artist': 1, 'title': 1, 'name': 1}", unique = true)
@Document(collection = "Songs")
data class SongDto(
        @Id
        @Generated
        val id: String?,

        @Field("artist")
        val artist: String,

        @Field("title")
        val title: String,

        @Field("name")
        val name: String,

        val background: String?,

        val youtube: String?,

        val status: String,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Europe/Amsterdam")
        val dateInserted: Instant = Instant.now(),

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Europe/Amsterdam")
        val dateModified: Instant = dateInserted,

        val userInserted: String,
        val userModified: String = userInserted

) {
    override fun toString(): String {
        return "Song(name=$artist, code=$title"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SongDto)
            return false
        return artist == other.artist && title == other.title && name == other.name
    }

    override fun hashCode(): Int {
        return Objects.hash(artist, title, name)
    }
}
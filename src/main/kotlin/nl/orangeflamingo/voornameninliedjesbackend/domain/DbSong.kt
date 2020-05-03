package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant
import java.util.*
import javax.annotation.Generated

@CompoundIndex(name = "song_uq_idx", def = "{'artist': 1, 'title': 1, 'name': 1}", unique = true)
@Document(collection = "Songs")
data class DbSong(
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

        val spotify: String?,

        val artistImage: String?,

        val wikimediaPhotos: Set<WikimediaPhoto> = setOf(),

        val flickrPhotos: Set<String> = setOf(),

        val sources: Set<Source> = setOf(),

        val logs: List<LogEntry> = listOf(),

        val status: SongStatus?
) {
    override fun toString(): String {
        return "Song(name=$artist, title=$title)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is DbSong)
            return false
        return artist == other.artist && title == other.title && name == other.name
    }

    override fun hashCode(): Int {
        return Objects.hash(artist, title, name)
    }
}

data class WikimediaPhoto(
        val url: String,
        val attribution: String
)

data class Source(
        val url: String,
        val name: String
)

data class LogEntry(
        val date: Instant,
        val user: String
)
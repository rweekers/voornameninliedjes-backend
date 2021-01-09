package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import org.springframework.util.Assert.notNull
import java.time.Instant


@Table("songs")
class Song(

    @Id
    var id: Long? = null,
    val title: String,
    val name: String,
    val artistImage: String? = null,
    val background: String? = null,
    val youtube: String? = null,
    val spotify: String? = null,
    val status: SongStatus,
    val mongoId: String? = null,
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    val sources: MutableList<SongSource> = mutableListOf(),
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    val logEntries: MutableList<SongLogEntry> = mutableListOf(),
    val artists: MutableSet<ArtistRef> = mutableSetOf(),
) {
    fun addArtist(artist: Artist, originalArtist: Boolean = true) {
        artists.add(createArtistRef(artist, originalArtist))
    }

    private fun createArtistRef(artist: Artist, originalArtist: Boolean): ArtistRef {
        notNull(artist.id, "Artist id, must not be null")
        return ArtistRef(
            artist = artist.id ?: throw RuntimeException(),
            originalArtist = originalArtist
        )
    }
}

@Table("song_sources")
class SongSource(
    val url: String,
    val name: String
)

@Table("song_log_entries")
class SongLogEntry(
    val date: Instant,
    val username: String
)
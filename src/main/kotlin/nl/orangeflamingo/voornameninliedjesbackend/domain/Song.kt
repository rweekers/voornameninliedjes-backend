package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import org.springframework.util.Assert.notNull
import java.lang.IllegalStateException
import java.time.Instant


@Table("songs")
data class Song(

    @Id
    var id: Long? = null,
    var title: String,
    var name: String,
    val artistImage: String? = null,
    var background: String? = null,
    var youtube: String? = null,
    var spotify: String? = null,
    var status: SongStatus,
    val mongoId: String? = null,
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    var sources: List<SongSource> = listOf(),
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    val logEntries: MutableList<SongLogEntry> = mutableListOf(),
    val artists: MutableSet<ArtistRef> = mutableSetOf()
) {
    fun addArtist(artist: Artist, originalArtist: Boolean = true) {
        artists.add(createArtistRef(artist, originalArtist))
    }

    private fun createArtistRef(artist: Artist, originalArtist: Boolean): ArtistRef {
        notNull(artist.id, "Artist id, must not be null")
        return ArtistRef(
            artist = artist.id ?: throw IllegalStateException("The artist should have an id"),
            originalArtist = originalArtist
        )
    }
}

@Table("song_sources")
data class SongSource(
    val url: String,
    val name: String
)

@Table("song_log_entries")
data class SongLogEntry(
    val date: Instant,
    val username: String
)
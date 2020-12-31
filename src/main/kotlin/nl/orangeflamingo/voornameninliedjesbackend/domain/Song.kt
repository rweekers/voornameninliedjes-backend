package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("songs")
class Song(

    @Id
    var id: Long? = null,
    val artistId: Long,
    val title: String,
    val name: String,
    val artistImage: String?,
    val background: String?,
    val youtube: String?,
    val spotify: String?,
    val status: SongStatus,
    val mongoId: String?,
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    val sources: MutableList<SongSource>,
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    val logEntries: MutableList<SongLogEntry>
)

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
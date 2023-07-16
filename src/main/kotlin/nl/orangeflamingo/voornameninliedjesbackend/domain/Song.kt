package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("songs")
data class Song(

    @Id
    var id: Long? = null,
    var title: String,
    var name: String,
    val artistImage: String? = null,
    var artistImageWidth: Int? = null,
    var artistImageHeight: Int? = null,
    val artistImageAttribution: String? = null,
    val localImage: String? = null,
    val blurredImage: String? = null,
    var background: String? = null,
    var youtube: String? = null,
    var spotify: String? = null,
    var wikipediaPage: String? = null,
    var status: SongStatus,
    var hasDetails: Boolean = false,
    var remarks: String? = null,
    var mbid: String? = null,
    var lastFmUrl: String? = null,
    var wikiSummaryEn: String? = null,
    var wikiContentEn: String? = null,
    var wikiContentNl: String? = null,
    var albumName: String? = null,
    var albumMbid: String? = null,
    var albumLastFmUrl: String? = null,
    val mongoId: String? = null,
    @MappedCollection(idColumn = "song_id")
    var wikimediaPhotos: MutableSet<SongWikimediaPhoto> = mutableSetOf(),
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    var sources: List<SongSource> = listOf(),
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    val logEntries: MutableList<SongLogEntry> = mutableListOf(),
    @MappedCollection(idColumn = "song_id", keyColumn = "song_key")
    val lastFmTags: MutableSet<SongLastFmTag> = mutableSetOf(),
    @Column("artist_id")
    var artist: AggregateReference<Artist, Long>
)

@Table("song_wikimedia_photos")
data class SongWikimediaPhoto(
    val url: String,
    val attribution: String
)

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

@Table("song_last_fm_tags")
data class SongLastFmTag(
    val name: String,
    val url: String
)
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
    @MappedCollection(idColumn = "song_id", keyColumn = "id")
    var wikimediaPhotos: MutableList<SongWikimediaPhoto> = mutableListOf(),
    @MappedCollection(idColumn = "song_id", keyColumn = "id")
    var sources: List<SongSource> = listOf(),
    @MappedCollection(idColumn = "song_id", keyColumn = "id")
    val logEntries: MutableList<SongLogEntry> = mutableListOf(),
    @MappedCollection(idColumn = "song_id", keyColumn = "id")
    val lastFmTags: MutableList<SongLastFmTag> = mutableListOf(),
    @Column("artist_id")
    var artist: AggregateReference<Artist, Long>
)

@Table("song_wikimedia_photos")
data class SongWikimediaPhoto(
    @Id
    var id: Long? = null,
    val url: String,
    val attribution: String
)

@Table("song_sources")
data class SongSource(
    @Id
    var id: Long? = null,
    val url: String,
    val name: String
)

@Table("song_log_entries")
data class SongLogEntry(
    @Id
    var id: Long? = null,
    val date: Instant,
    val username: String
)

@Table("song_last_fm_tags")
data class SongLastFmTag(
    @Id
    var id: Long? = null,
    val name: String,
    val url: String
)
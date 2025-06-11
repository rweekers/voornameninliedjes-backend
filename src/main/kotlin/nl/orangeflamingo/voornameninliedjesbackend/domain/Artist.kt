package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.net.URI
import java.time.Instant
import java.util.UUID

@Table("artists")
data class Artist(

    @Id
    var id: Long? = null,
    var name: String,
    var mbid: UUID? = null,
    var lastFmUrl: URI? = null,
    val background: String? = null,
    @MappedCollection(idColumn = "artist_id")
    var photos: MutableSet<ArtistPhoto> = mutableSetOf(),
    @MappedCollection(idColumn = "artist_id")
    val logEntries: MutableSet<ArtistLogEntry> = mutableSetOf()
)

@Table("artist_photos")
data class ArtistPhoto(
    @Id
    var id: Long? = null,
    val url: String,
    val attribution: String
)

@Table("artist_log_entries")
data class ArtistLogEntry(
    @Id
    var id: Long? = null,
    val date: Instant,
    val username: String
)
package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("artists")
data class Artist(

    @Id
    var id: Long? = null,
    var name: String,
    var mbid: String? = null,
    var lastFmUrl: String? = null,
    val background: String? = null,
    @MappedCollection(idColumn = "artist_id", keyColumn = "id")
    var wikimediaPhotos: MutableList<ArtistWikimediaPhoto> = mutableListOf(),
    @MappedCollection(idColumn = "artist_id", keyColumn = "id")
    var flickrPhotos: MutableList<ArtistFlickrPhoto> = mutableListOf(),
    @MappedCollection(idColumn = "artist_id", keyColumn = "id")
    val logEntries: MutableList<ArtistLogEntry> = mutableListOf()
)

@Table("artist_wikimedia_photos")
data class ArtistWikimediaPhoto(
    @Id
    var id: Long? = null,
    val url: String,
    val attribution: String
)

@Table("artist_flickr_photos")
data class ArtistFlickrPhoto(
    @Id
    var id: Long? = null,
    val flickrId: String
)

@Table("artist_log_entries")
class ArtistLogEntry(
    @Id
    var id: Long? = null,
    val date: Instant,
    val username: String
)
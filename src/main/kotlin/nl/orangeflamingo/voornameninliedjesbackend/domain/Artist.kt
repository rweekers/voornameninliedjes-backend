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
    @MappedCollection(idColumn = "artist_id")
    var wikimediaPhotos: MutableSet<ArtistWikimediaPhoto> = mutableSetOf(),
    @MappedCollection(idColumn = "artist_id")
    var flickrPhotos: MutableSet<ArtistFlickrPhoto> = mutableSetOf(),
    @MappedCollection(idColumn = "artist_id", keyColumn = "artist_key")
    val logEntries: MutableList<ArtistLogEntry> = mutableListOf()
)

@Table("artist_wikimedia_photos")
data class ArtistWikimediaPhoto(
    val url: String,
    val attribution: String
)

@Table("artist_flickr_photos")
data class ArtistFlickrPhoto(
    val flickrId: String
)

@Table("artist_log_entries")
class ArtistLogEntry(
    val date: Instant,
    val username: String
)
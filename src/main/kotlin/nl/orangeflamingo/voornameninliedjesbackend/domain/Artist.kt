package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("artists")
class Artist(

    @Id
    var id: Long? = null,
    val name: String,
    val background: String?,
    @MappedCollection(idColumn = "artist_id")
    val wikimediaPhotos: MutableSet<ArtistWikimediaPhoto>,
    @MappedCollection(idColumn = "artist_id")
    val flickrPhotos: MutableSet<ArtistFlickrPhoto>,
    @MappedCollection(idColumn = "artist_id", keyColumn = "artist_key")
    val logEntries: MutableList<ArtistLogEntry>
) {
    fun addWikimediaPhoto(wikimediaPhoto: ArtistWikimediaPhoto) {
        this.wikimediaPhotos.add(wikimediaPhoto)
    }

    fun addFlickrPhoto(flickrPhotoPhoto: ArtistFlickrPhoto) {
        this.flickrPhotos.add(flickrPhotoPhoto)
    }
}

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
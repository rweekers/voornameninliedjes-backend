package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*
import javax.annotation.Generated

@Document(collation = "Songs")
data class MongoSong(
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

    val flickrPhotos: Set<PhotoDetail> = setOf(),

    val sources: Set<SourceDetail> = setOf(),

    val status: SongStatus?
) {
    override fun toString(): String {
        return "Song(name=$artist, title=$title, name=$name)"
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

data class PhotoDetail(
        val url: String,
        val farm: String,
        val server: String,
        val id: String,
        val title: String,
        val secret: String,
        val licenseDetail: License?,
        val ownerDetail: Owner?
)

data class License(
        val id: String,
        val name: String,
        val url: String
)

data class Owner(
        val id: String,
        val username: String,
        val photosUrl: String
)

data class SourceDetail(
        val url: String,
        val name: String
)
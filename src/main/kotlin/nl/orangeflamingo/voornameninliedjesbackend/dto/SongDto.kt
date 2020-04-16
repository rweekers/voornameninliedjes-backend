package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonView
import nl.orangeflamingo.voornameninliedjesbackend.controller.Views
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*
import javax.annotation.Generated

data class SongDto(
        @Id
        @Generated
        @JsonView(Views.Summary::class, Views.Detail::class)
        val id: String?,

        @JsonView(Views.Summary::class, Views.Detail::class)
        @Field("artist")
        val artist: String,

        @JsonView(Views.Summary::class, Views.Detail::class)
        @Field("title")
        val title: String,

        @JsonView(Views.Summary::class, Views.Detail::class)
        @Field("name")
        val name: String,

        @JsonView(Views.Summary::class, Views.Detail::class)
        @Field("artistImage")
        val artistImage: String?,

        @JsonView(Views.Detail::class)
        val background: String?,

        @JsonView(Views.Detail::class)
        val youtube: String?,

        @JsonView(Views.Detail::class)
        val spotify: String?,

        @JsonView(Views.Detail::class)
        val wikimediaPhotos: Set<WikimediaPhotoDto>,

        @JsonView(Views.Detail::class)
        val flickrPhotos: Set<String>,

        @JsonView(Views.Detail::class)
        val status: String?

) {
    override fun toString(): String {
        return "Song(name=$artist, code=$title)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SongDto)
            return false
        return artist == other.artist && title == other.title && name == other.name
    }

    override fun hashCode(): Int {
        return Objects.hash(artist, title, name)
    }
}
data class WikimediaPhotoDto(
        @JsonView(Views.Detail::class)
        val url: String,
        @JsonView(Views.Detail::class)
        val attribution: String
)

data class PhotoDto(
        val farm: String,
        val server: String,
        val id: String,
        val secret: String
)

data class FlickrPhotoDto(
        val photo: PhotoDto
)
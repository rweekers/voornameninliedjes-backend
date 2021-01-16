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
        val wikimediaPhotos: Set<WikimediaPhotoDto>?,

        @JsonView(Views.Detail::class)
        val flickrPhotos: Set<PhotoDto>,

        @JsonView(Views.Detail::class)
        val sources: Set<SourceDto>,

        @JsonView(Views.Detail::class)
        val status: String

) {
    override fun toString(): String {
        return "Song(name=$artist, code=$title, name=$name)"
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

data class FlickrOwnerDto(
        @JsonView(Views.Detail::class)
        val id: String,
        @JsonView(Views.Detail::class)
        val username: String,
        @JsonView(Views.Detail::class)
        val photoUrl: String
)

data class FlickrLicenseDto(
        @JsonView(Views.Detail::class)
        val name: String,
        @JsonView(Views.Detail::class)
        val url: String
)

data class PhotoDto(
        @JsonView(Views.Detail::class)
        val url: String,
        @JsonView(Views.Detail::class)
        val farm: String,
        @JsonView(Views.Detail::class)
        val server: String,
        @JsonView(Views.Detail::class)
        val id: String,
        @JsonView(Views.Detail::class)
        val secret: String,
        @JsonView(Views.Detail::class)
        val title: String,
        @JsonView(Views.Detail::class)
        val owner: FlickrOwnerDto,
        @JsonView(Views.Detail::class)
        val license: FlickrLicenseDto
)

data class SourceDto(
        @JsonView(Views.Detail::class)
        val url: String,
        @JsonView(Views.Detail::class)
        val name: String
)

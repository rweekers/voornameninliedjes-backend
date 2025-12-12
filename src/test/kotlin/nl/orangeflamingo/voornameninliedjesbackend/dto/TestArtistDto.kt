package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import java.net.URI
import java.util.*

data class TestArtistDto @JsonCreator constructor(

    @param:JsonProperty("id") val id: Long,
    @param:JsonProperty("name") val name: String,
    @param:JsonProperty("photos") @param:JsonSetter(nulls = Nulls.AS_EMPTY) val photos: List<TestPhotoDto>,
    @param:JsonProperty("wikimediaPhotos") @param:JsonSetter(nulls = Nulls.AS_EMPTY) val wikimediaPhotos: Set<TestArtistWikimediaPhotoDto>,
    @param:JsonProperty("flickrPhotos") @param:JsonSetter(nulls = Nulls.AS_EMPTY) val flickrPhotos: Set<TestArtistFlickrPhotoDto>,
    @param:JsonProperty("mbid") val mbid: UUID? = null,
    @param:JsonProperty("lastFmUrl") val lastFmUrl: URI? = null,
    @param:JsonProperty("imageUrl") val imageUrl: URI? = null
)

data class TestPhotoDto @JsonCreator constructor(

    @param:JsonProperty("url") val url: URI,
    @param:JsonProperty("attribution") val attribution: String
)

data class TestArtistWikimediaPhotoDto @JsonCreator constructor(
    @param:JsonProperty("url") val url: String,
    @param:JsonProperty("attribution") val attribution: String
)

data class TestArtistFlickrPhotoDto @JsonCreator constructor(
    @param:JsonProperty("flickrId") val flickrId: String
)
package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class TestSongPageDto @JsonCreator constructor(
    @param:JsonProperty("songs") val songs: List<TestSongDto>,
    @param:JsonProperty("totalItems") val totalItems: Long,
    @param:JsonProperty("isLast") val isLast: Boolean?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TestSongDto @JsonCreator constructor(
    @param:JsonProperty("id") val id: String? = null,
    @param:JsonProperty("artist") val artist: String? = "The Beatles",
    @param:JsonProperty("title") val title: String? = "Lucy in the Sky with Diamonds",
    @param:JsonProperty("name") val name: String? = "Lucy",
    @param:JsonProperty("artistImage") val artistImage: String? = null,
    @param:JsonProperty("artistImageAttribution") val artistImageAttribution: String? = null,
    @param:JsonProperty("background") val background: String? = "Some background on Lucy in the Sky with Diamonds",
    @param:JsonProperty("artistLastFmUrl") val artistLastFmUrl: String? = "https://last.fm/music/The+Beatles",
    @param:JsonProperty("wikipediaPage") val wikipediaPage: String? = "wikiPage",
    @param:JsonProperty("youtube") val youtube: String? = "",
    @param:JsonProperty("spotify") val spotify: String? = "",
    @param:JsonProperty("wikimediaPhotos") @param:JsonSetter(nulls = Nulls.AS_EMPTY) val wikimediaPhotos: Set<TestWikimediaPhotoDto> = emptySet(),
    @param:JsonProperty("flickrPhotos") @param:JsonSetter(nulls = Nulls.AS_EMPTY) val flickrPhotos: Set<PhotoDto> = emptySet(),
    @param:JsonProperty("sources") @param:JsonSetter(nulls = Nulls.AS_EMPTY) val sources: Set<TestSourceDto> = emptySet(),
    @param:JsonProperty("tags") @param:JsonSetter(nulls = Nulls.AS_EMPTY) val tags: Set<TestLastFmTagDto>,
    @param:JsonProperty("status") val status: String? = "SHOW",
    @param:JsonProperty("hasDetails") val hasDetails: Boolean = false
)

data class TestWikimediaPhotoDto @JsonCreator constructor(
    @param:JsonProperty("url") val url: String,
    @param:JsonProperty("attribution") val attribution: String
)

data class TestSourceDto @JsonCreator constructor(
    @param:JsonProperty("url") val url: String,
    @param:JsonProperty("name") val name: String
)

data class TestLastFmTagDto @JsonCreator constructor(
    @param:JsonProperty("name") val name: String,
    @param:JsonProperty("url") val url: String
)
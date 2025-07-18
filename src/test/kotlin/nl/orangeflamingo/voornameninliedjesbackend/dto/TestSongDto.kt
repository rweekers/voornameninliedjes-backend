package nl.orangeflamingo.voornameninliedjesbackend.dto

data class TestSongPageDto(
    val songs: List<TestSongDto> = emptyList(),
    val totalItems: Long = 0,
    val isLast: Boolean = false
)

data class TestSongDto(
    val id: String? = null,
    val artist: String = "The Beatles",
    val title: String = "Lucy in the Sky with Diamonds",
    val name: String = "Lucy",
    val artistImage: String? = null,
    val artistImageAttribution: String? = null,
    val background: String? = "Some background on Lucy in the Sky with Diamonds",
    val wikipediaPage: String? = "wikiPage",
    val youtube: String? = "",
    val spotify: String? = "",
    val wikimediaPhotos: Set<WikimediaPhotoDto> = emptySet(),
    val flickrPhotos: Set<PhotoDto> = emptySet(),
    val sources: Set<SourceDto> = emptySet(),
    val status: String = "SHOW",
    val hasDetails: Boolean = false
)
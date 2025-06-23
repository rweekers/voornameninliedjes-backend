package nl.orangeflamingo.voornameninliedjesbackend.domain

data class SongDetail(
    val artist: String,
    val title: String,
    val name: String,
    val hasDetails: Boolean = false,
    val youtube: String? = null,
    val spotify: String? = null,
    val background: String? = null,
    val artistImage: String? = null,
    val artistImageAttribution: String? = null,
    val localImage: String? = null,
    val blurredImage: String? = null,
    val artistImageWidth: Int? = null,
    val artistImageHeight: Int? = null,
    val wikipediaPage: String? = null,
    val wikiContentNl: String? = null,
    val wikiContentEn: String? = null,
    val wikiSummaryEn: String? = null,
    val lastfmAlbum: LastFmAlbum? = null,
    val photos: List<Photo> = emptyList(),
    val sources: List<SongSource> = emptyList(),
    val tags: List<SongLastFmTag> = emptyList()
)

data class Photo(val url: String, val attribution: String) {}
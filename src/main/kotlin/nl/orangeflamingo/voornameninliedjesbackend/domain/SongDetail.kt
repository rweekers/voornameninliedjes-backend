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
    val photos: List<Photo> = emptyList()
)

data class Photo(val url: String, val attribution: String) {}
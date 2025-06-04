package nl.orangeflamingo.voornameninliedjesbackend.domain

data class SongDetail(
    val artist: String,
    val title: String,
    val name: String,
    val hasDetails: Boolean = false,
    val youtube: String?,
    val spotify: String?,
    val background: String?,
    val artistImage: String?,
    val artistImageAttribution: String?,
    val localImage: String?,
    val blurredImage: String?,
    val artistImageWidth: Int?,
    val artistImageHeight: Int?,
    val photos: List<Photo>
)

data class Photo(val url: String, val attribution: String) {}
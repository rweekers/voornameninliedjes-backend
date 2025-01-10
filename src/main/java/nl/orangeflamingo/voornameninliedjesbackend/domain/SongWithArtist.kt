package nl.orangeflamingo.voornameninliedjesbackend.domain

data class SongWithArtist(
    val artist: String,
    val title: String,
    val name: String,
    val hasDetails: Boolean = false,
    val artistImage: String?,
    val artistImageAttribution: String?,
    val localImage: String?,
    val blurredImage: String?,
    val artistImageWidth: Int?,
    val artistImageHeight: Int?
) {}
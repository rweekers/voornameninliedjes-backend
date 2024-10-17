package nl.orangeflamingo.voornameninliedjesbackend.domain

data class SongNameStatistics(
    var name: String,
    var count: Int,
)

data class ArtistNameStatistics(
    var name: String,
    var count: Int,
)

data class SongStatistics(
    val count: Long
)
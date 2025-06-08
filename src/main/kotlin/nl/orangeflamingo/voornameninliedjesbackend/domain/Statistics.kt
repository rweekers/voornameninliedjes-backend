package nl.orangeflamingo.voornameninliedjesbackend.domain

data class SongNameStatistics(
    val name: String,
    val count: Int,
)

data class ArtistNameStatistics(
    val name: String,
    val count: Int,
)

data class SongStatistics(
    val count: Long
)

data class SongStatusStatistics(
    val status: SongStatus,
    val count: Long,
)

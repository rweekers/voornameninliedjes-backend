package nl.orangeflamingo.voornameninliedjesbackend.domain

data class SongNameFirstCharacterStatistics(
    var name: String,
    var count: Int,
)

data class SongNameStatistics(
    var name: String,
    var count: Int,
)

data class ArtistNameStatistics(
    var name: String,
    var count: Int,
)
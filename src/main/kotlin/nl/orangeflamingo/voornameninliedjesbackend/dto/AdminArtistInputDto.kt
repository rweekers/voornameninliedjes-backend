package nl.orangeflamingo.voornameninliedjesbackend.dto

data class AdminArtistInputDto(
    val name: String,
    val background: String?,
    val imageUrl: String?,
    val imageAttribution: String?
)
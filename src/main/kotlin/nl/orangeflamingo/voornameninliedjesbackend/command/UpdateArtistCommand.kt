package nl.orangeflamingo.voornameninliedjesbackend.command

data class UpdateArtistCommand(
    val id: Long,
    val name: String,
    val background: String?,
    val imageUrl: String?,
    val imageAttribution: String?
)
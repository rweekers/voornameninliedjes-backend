package nl.orangeflamingo.voornameninliedjesbackend.command

data class CreateArtistCommand(
    val name: String,
    val background: String?,
    val imageUrl: String?,
    val imageAttribution: String?
)
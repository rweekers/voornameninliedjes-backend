package nl.orangeflamingo.voornameninliedjesbackend.command

import java.net.URI
import java.util.UUID

data class CreateArtistCommand(
    val name: String,
    val mbid: UUID? = null,
    val lastFmUrl: URI? = null,
    val background: String? = null,
    val photos: Set<ArtistPhotoCommand> = emptySet()
)

data class ArtistPhotoCommand(
    val url: URI,
    val attribution: String
)
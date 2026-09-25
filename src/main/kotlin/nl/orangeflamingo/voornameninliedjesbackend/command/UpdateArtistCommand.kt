package nl.orangeflamingo.voornameninliedjesbackend.command

import java.net.URI
import java.util.UUID

data class UpdateArtistCommand(
    val id: Long,
    val name: String,
    val background: String?,
    val mbid: UUID?,
    val lastFmUrl: URI?,
    val photos: Set<ArtistPhotoCommand>
)
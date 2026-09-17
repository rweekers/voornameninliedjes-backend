package nl.orangeflamingo.voornameninliedjesbackend.dto

import java.net.URI
import java.util.UUID

data class AdminArtistInputDto(
    val name: String,
    val background: String?,
    val lastFmUrl: URI?,
    val mbid: UUID?,
    val photos: Set<AdminArtistPhotoInputDto> = emptySet()
)

data class AdminArtistPhotoInputDto(
    val imageUrl: URI,
    val imageAttribution: String
)
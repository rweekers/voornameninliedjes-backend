package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.relational.core.mapping.Table

@Table("songs_artists")
data class ArtistRef(
    var artist: Long? = null
)
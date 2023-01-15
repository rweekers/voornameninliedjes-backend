package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("songs_artists")
data class ArtistRef(
    @Column("artists")
    val artist: Long,
    val originalArtist: Boolean = true
)
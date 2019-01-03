package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id

data class Song(
        @Id
        val id: String,
        val artist: String,
        val title: String,
        val name: String
)
package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Songs")
data class Song(
        @Id
        val id: String,
        val artist: String,
        val title: String,
        val name: String
)
package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("artists")
class Artist(

    @Id
    val id: Long? = null,
    val name: String
)
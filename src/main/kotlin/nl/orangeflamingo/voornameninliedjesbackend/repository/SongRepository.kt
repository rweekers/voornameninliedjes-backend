package nl.orangeflamingo.voornameninliedjesbackend.repository

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface SongRepository : ReactiveCrudRepository<Song, String>
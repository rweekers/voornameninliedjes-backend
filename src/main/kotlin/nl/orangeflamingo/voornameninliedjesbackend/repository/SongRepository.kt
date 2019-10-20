package nl.orangeflamingo.voornameninliedjesbackend.repository

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface SongRepository : ReactiveCrudRepository<Song, String> {

    fun findAllByStatusOrderByName(status: SongStatus): Flux<Song>

}
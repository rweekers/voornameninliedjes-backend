package nl.orangeflamingo.voornameninliedjesbackend.repository.mongo

import nl.orangeflamingo.voornameninliedjesbackend.domain.DbSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface SongRepository : ReactiveCrudRepository<DbSong, String> {

    fun findAllByStatusOrderByName(status: SongStatus): Flux<DbSong>

    fun findAllByStatusAndArtistImageIsNull(status: SongStatus): Flux<DbSong>

}
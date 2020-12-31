package nl.orangeflamingo.voornameninliedjesbackend.repository.mongo

import nl.orangeflamingo.voornameninliedjesbackend.domain.DbSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoSongRepository : MongoRepository<DbSong, String> {

    fun findAllByStatusOrderByName(status: SongStatus): List<DbSong>

    fun findAllByStatusAndArtistImageIsNull(status: SongStatus): List<DbSong>

}
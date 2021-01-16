package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SongRepository : CrudRepository<Song, Long> {

    @Query("select * from songs where status = CAST(:status AS SONG_STATUS) order by name ASC")
    fun findAllByStatusOrderedByName(@Param("status") status: SongStatus): List<Song>

    fun findAllByNameStartingWithIgnoreCaseOrderByNameAsc(firstCharacter: String): List<Song>

    fun findAllByNameIgnoreCaseOrderByNameAsc(name: String): List<Song>

    @Query("update songs set status = CAST(:status AS SONG_STATUS)")
    fun updateAllSongStatus(@Param("status") status: SongStatus)

}
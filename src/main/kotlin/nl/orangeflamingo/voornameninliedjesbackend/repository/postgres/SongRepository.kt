package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface SongRepository : PagingAndSortingRepository<Song, Long> {

    @Query("select * from songs order by name ASC")
    fun findAllOrderedByName(): List<Song>

}
package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.SongNameStatistics
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SongNameStatisticsRepository : CrudRepository<SongNameStatistics, Long> {

    @Query("select name, count(1) as count from songs where status = 'SHOW' group by name order by count DESC, name")
    fun findAllStatusShowGroupedByNameOrderedByCountDescending(): List<SongNameStatistics>
}
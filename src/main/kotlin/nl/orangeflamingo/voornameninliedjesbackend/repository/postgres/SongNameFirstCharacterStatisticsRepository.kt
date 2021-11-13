package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.SongNameFirstCharacterStatistics
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SongNameFirstCharacterStatisticsRepository : CrudRepository<SongNameFirstCharacterStatistics, Long> {

    @Query("select lower(left(name, 1)) as name, count(1) as count from songs where status = 'SHOW' group by lower(left(name, 1)) order by lower(left(name, 1))")
    fun findAllStatusShowGroupedByNameStartingWithOrderedByName(): List<SongNameFirstCharacterStatistics>
}
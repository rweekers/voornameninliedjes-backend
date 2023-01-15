package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistNameStatistics
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArtistNameStatisticsRepository : CrudRepository<ArtistNameStatistics, Long> {

    @Query("select a.name, count(s.id) as count from songs s inner join songs_artists sa on s.id = sa.songs\n" +
            "inner join artists a on sa.artists = a.id\n" +
            "group by a.name\n" +
            "order by count DESC, a.name")
    fun getCountPerArtistname(): List<ArtistNameStatistics>
}
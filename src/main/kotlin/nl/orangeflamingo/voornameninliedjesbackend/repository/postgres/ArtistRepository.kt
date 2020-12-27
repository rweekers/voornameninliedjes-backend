package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArtistRepository : PagingAndSortingRepository<Artist, Long> {

    @Query("select * from artists where name=:name")
    fun findByName(@Param("name") name: String): List<Artist>

    @Query("select * from artists order by name ASC")
    fun findAllOrderedByName(): List<Artist>
}
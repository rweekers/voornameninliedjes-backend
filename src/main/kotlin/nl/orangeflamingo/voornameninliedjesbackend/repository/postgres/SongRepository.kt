package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SongRepository : CrudRepository<Song, Long> {

    @Query("select * from songs where status = :status order by name ASC")
    fun findAllByStatusOrderedByName(@Param("status") status: String): List<Song>

    @Query("select * from songs where status = :status order by name ASC")
    fun findAllByStatusAndArtistImageIsNull(@Param("status") status: String): List<Song>

    fun findAllByNameStartingWithIgnoreCaseAndStatusInOrderByNameAsc(firstCharacter: String, status: List<String>): List<Song>

    fun findAllByNameIgnoreCaseOrderByNameAsc(name: String): List<Song>

    fun findFirstByTitle(title: String): Optional<Song>

    @Query("select * from songs order by name ASC")
    fun findAllOrderByNameAsc(): List<Song>

    @Query("update songs set status = :status")
    fun updateAllSongStatus(@Param("status") status: SongStatus)

    @Query("select s.* from songs s\n" +
            "inner join songs_artists sa on s.id = sa.song\n" +
            "inner join artists a on artist = a.id\n" +
            "where lower(replace(replace(a.name, '?', ''), '/', '')) = lower(replace(replace(:artist, '?', ''), '/', ''))\n" +
            "and lower(s.title) = lower(:title)")
    fun findByArtistAndTitle(@Param("artist") artist: String, @Param("title") title: String): Optional<Song>

}
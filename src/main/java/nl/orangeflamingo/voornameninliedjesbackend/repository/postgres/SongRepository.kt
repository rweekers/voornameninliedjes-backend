package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatusStatistics
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SongRepository : CrudRepository<Song, Long> {

    @Query("select * from songs where status = :status order by name, title")
    fun findAllByStatusOrderedByNameAndTitle(@Param("status") status: String): List<Song>

    @Query("select * from songs where status in (:statuses) and lower(left(name, 1)) in (:firstChars) order by name, title")
    fun findAllByStatusesAndNameStartingWithOrderedByNameAndTitle(@Param("statuses") statuses: List<String>, @Param("firstChars") firstChars: List<String>): List<Song>

    @Query("select * from songs where status = :status and (artist_image is null or artist_image_attribution is null) order by name")
    fun findAllByStatusAndArtistImageIsNullOrArtistImageAttributionIsNull(@Param("status") status: String): List<Song>

    @Query("select * from songs where status = :status and wikipedia_page <> '' and wiki_content_nl is null order by name, title")
    fun findAllByStatusAndWikipediaPageIsNotNullAndWikiContentNlIsNullOrderedByNameAndTitle(@Param("status") status: String): List<Song>

    @Query("select * from songs where status = :status and last_fm_url is null order by name, title")
    fun findAllByStatusAndLastFmUrlIsNullOrderedByNameAndTitle(@Param("status") status: String): List<Song>

    fun findAllByNameStartingWithIgnoreCaseAndStatusInOrderByNameAscTitleAsc(firstCharacter: String, status: Collection<SongStatus>): List<Song>

    fun findAllByNameIgnoreCaseOrderByNameAscTitleAsc(name: String): List<Song>

    fun findFirstByTitle(title: String): Optional<Song>

    @Query("select * from songs order by name, title")
    fun findAllOrderByNameAscTitleAsc(): List<Song>

    @Query("update songs set status = :status")
    fun updateAllSongStatus(@Param("status") status: SongStatus)

    @Query("select s.* from songs s\n" +
            "inner join artists a on s.artist_id = a.id\n" +
            "where lower(replace(replace(a.name, '?', ''), '/', '')) = lower(replace(replace(:artist, '?', ''), '/', ''))\n" +
            "and lower(replace(replace(s.title, '?', ''), '#', '')) = lower(replace(replace(:title, '?', ''), '#', ''))")
    fun findByArtistAndTitle(@Param("artist") artist: String, @Param("title") title: String): Optional<Song>

    fun countAllByStatusIs(status: SongStatus): Long

    @Query("select status, count(1) as count from songs group by status order by count DESC, status")
    fun getCountPerStatus(): List<SongStatusStatistics>

}
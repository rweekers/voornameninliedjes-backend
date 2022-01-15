package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SongRepository : CrudRepository<Song, Long> {

    @Query("select * from songs where status = :status order by name ASC, title ASC")
    fun findAllByStatusOrderedByNameAndTitle(@Param("status") status: String): List<Song>

    @Query("select * from songs where status in (:statuses) and lower(left(name, 1)) in (:firstChars) order by name ASC, title ASC")
    fun findAllByStatusesAndNameStartingWithOrderedByNameAndTitle(@Param("statuses") statuses: List<String>, @Param("firstChars") firstChars: List<String>): List<Song>

    @Query("select * from songs where status = :status and (artist_image is null or artist_image_attribution is null) order by name ASC")
    fun findAllByStatusAndArtistImageIsNullOrArtistImageAttributionIsNull(@Param("status") status: String): List<Song>

    @Query("select * from songs where status = :status and wikipedia_page <> '' and wiki_content_nl is null order by name ASC, title ASC")
    fun findAllByStatusAndWikipediaPageIsNotNullAndWikiContentNlIsNullOrderedByNameAndTitle(@Param("status") status: String): List<Song>

    @Query("select * from songs where status = :status and last_fm_url is null order by name ASC, title ASC")
    fun findAllByStatusAndLastFmUrlIsNullOrderedByNameAndTitle(@Param("status") status: String): List<Song>

    fun findAllByNameStartingWithIgnoreCaseAndStatusInOrderByNameAscTitleAsc(firstCharacter: String, status: List<String>): List<Song>

    fun findAllByNameIgnoreCaseOrderByNameAscTitleAsc(name: String): List<Song>

    fun findFirstByTitle(title: String): Optional<Song>

    @Query("select * from songs order by name ASC, title ASC")
    fun findAllOrderByNameAscTitleAsc(): List<Song>

    @Query("update songs set status = :status")
    fun updateAllSongStatus(@Param("status") status: SongStatus)

    @Query("select s.* from songs s\n" +
            "inner join songs_artists sa on s.id = sa.song\n" +
            "inner join artists a on artist = a.id\n" +
            "where lower(replace(replace(a.name, '?', ''), '/', '')) = lower(replace(replace(:artist, '?', ''), '/', ''))\n" +
            "and lower(replace(replace(s.title, '?', ''), '#', '')) = lower(replace(replace(:title, '?', ''), '#', ''))")
    fun findByArtistAndTitle(@Param("artist") artist: String, @Param("title") title: String): Optional<Song>

}
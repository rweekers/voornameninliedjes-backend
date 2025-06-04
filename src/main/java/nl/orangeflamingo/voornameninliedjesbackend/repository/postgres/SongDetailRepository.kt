package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SongDetailRepository: CrudRepository<Song, Long> {

//    @Query(
//        """
//        SELECT a.name AS artist, s.title, s.name, s.has_details, s.artist_image, s.artist_image_attribution, s.local_image, s.blurred_image, s.artist_image_width, s.artist_image_height, COALESCE(jsonb_agg(jsonb_build_object('url', w.url, 'attribution', w.attribution))::TEXT, '[]') AS wikimedia_photos
//        FROM songs s
//        INNER JOIN artists a ON s.artist_id = a.id
//        LEFT JOIN song_wikimedia_photos w ON w.song_id = s.id
//        WHERE LOWER(a.name) = LOWER(:artist) AND LOWER(s.title) = LOWER(:title)
//        GROUP BY s.id, a.name
//        """
//    )
//    fun findByArtistAndTitle(@Param("artist") status: String,
//    @Param("title") name: String): SongDetail?

    // fun findSongDetailByArtistAndTitle(artist: String, title: String): SongDetail?

    @Query("select s.* from songs s\n" +
            "inner join artists a on s.artist_id = a.id\n" +
            "where lower(replace(replace(a.name, '?', ''), '/', '')) = lower(replace(replace(:artist, '?', ''), '/', ''))\n" +
            "and lower(replace(replace(s.title, '?', ''), '#', '')) = lower(replace(replace(:title, '?', ''), '#', ''))")
    fun findByArtistAndTitle(@Param("artist") artist: String, @Param("title") title: String): Optional<Song>
}
package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWithArtist
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface SongRepositoryV2: PagingAndSortingRepository<SongWithArtist, Long> {

    @Query(
        """
        SELECT a.name AS artist, s.title, s.name, s.has_details, s.artist_image, s.artist_image_attribution, s.local_image, s.blurred_image, s.artist_image_width, s.artist_image_height
        FROM songs s
        INNER JOIN artists a ON s.artist_id = a.id
        WHERE s.status = :status
        AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT(:name, '%')))
        ORDER BY s.name, s.title, a.name
        LIMIT :limit OFFSET :offset
        """
    )
    fun findAllSongsWithArtistsStartingWith(
        @Param("status") status: String,
        @Param("name") name: String?,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): List<SongWithArtist>

    @Query(
        """
        SELECT count(1)
        FROM songs s
        INNER JOIN artists a ON s.artist_id = a.id
        WHERE s.status = :status
        AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT(:name, '%')))
        """
    )
    fun countAllSongsWithArtistsStartingWith(
        @Param("status") status: String,
        @Param("name") name: String?
    ): Long

}
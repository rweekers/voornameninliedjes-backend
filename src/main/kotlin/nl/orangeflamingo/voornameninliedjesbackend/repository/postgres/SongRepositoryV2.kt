package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository


@Repository
interface SongRepositoryV2: PagingAndSortingRepository<Song, Long> {

    fun findByNameStartingWithAndStatusOrderByName(lastname: String?, status: SongStatus, pageable: Pageable): Page<Song>
}
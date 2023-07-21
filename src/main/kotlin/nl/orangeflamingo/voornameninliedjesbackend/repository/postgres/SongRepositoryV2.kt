package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository


@Repository
interface SongRepositoryV2: PagingAndSortingRepository<Song, Long> {

    fun findByNameStartingWithIgnoreCaseAndStatusOrderByName(firstChars: String, status: SongStatus, pageable: Pageable): Slice<Song>
}
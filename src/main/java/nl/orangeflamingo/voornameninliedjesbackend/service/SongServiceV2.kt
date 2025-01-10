package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWithArtist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepositoryV2
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SongServiceV2(
    private val songRepositoryV2: SongRepositoryV2
) {
    fun findByNameStartingWith(firstChars: String?, status: SongStatus, pageable: Pageable): List<SongWithArtist> {
        val s = songRepositoryV2
            .findAllSongsWithArtistsStartingWith(status.code, firstChars, pageable.pageSize, pageable.pageNumber)
        return s
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepositoryV2
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class SongServiceV2(
    private val artistRepository: ArtistRepository,
    private val songRepositoryV2: SongRepositoryV2
) {
    fun findByNameStartingWith(firstChars: String, status: SongStatus, pageable: Pageable): Slice<Pair<Song, Artist>> {
        return songRepositoryV2
            .findByNameStartingWithIgnoreCaseAndStatusOrderByName(firstChars, status, pageable)
            .map { song ->
                val artist = artistRepository.findById(song.artist.id ?: throw IllegalStateException("Song with title ${song.title} has no id"))
                    .orElseThrow { ArtistNotFoundException("Artist with artist id ${song.artist.id} for title ${song.title} not found") }
                Pair(song, artist)
            }
    }
}
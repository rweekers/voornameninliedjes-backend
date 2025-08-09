package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.PaginatedSongs
import nl.orangeflamingo.voornameninliedjesbackend.domain.Photo
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWithArtist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongDetailRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepositoryV2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.mapping.AggregateReference

class SongServiceV2Test {

    private val songRepositoryV2 = mockk<SongRepositoryV2>()
    private val songDetailRepository = mockk<SongDetailRepository>()
    private val artistRepository = mockk<ArtistRepository>()

    private val songServiceV2: SongServiceV2 = SongServiceV2(
        songRepositoryV2,
        songDetailRepository,
        artistRepository
    )

    @Test
    fun `get all songs`() {
        val song = mockk<SongWithArtist>()
        val status = SongStatus.SHOW
        val name = "Al"
        val limit = 10
        val page = 3
        every { songRepositoryV2.findAllSongsWithArtistsStartingWith(status.code, name, limit, page) } returns
                listOf(song)
        every { songRepositoryV2.countAllSongsWithArtistsStartingWith(status.code, name) } returns 41
        val songPage = songServiceV2.findByNameStartingWith(name, status, Pageable.ofSize(limit).withPage(page))
        assertThat(songPage).isEqualTo(PaginatedSongs(listOf(song), 41, false))
        verify { songRepositoryV2.findAllSongsWithArtistsStartingWith(status = "SHOW", name = "Al", limit = 10, offset = page) }
    }

    @Test
    fun `find by artist and title`() {
        val artistString = "Paul Simon"
        val titleString = "You Can Call Me Al"
        val nameString = "Al"
        val photoUrl = "https://someUrl"
        val photoAttribution = "Some attribution"
        val artistEntity = Artist(name = artistString)
        val artistId = 1L
        val artist = AggregateReference.to<Artist, Long>(artistId)
        val songEntity = Song(
            title = titleString,
            artist = artist,
            name = nameString,
            artistImageHeight = 0,
            artistImageWidth = 0,
            status = SongStatus.SHOW,
            photos = mutableSetOf(
                SongPhoto(url = photoUrl, attribution = photoAttribution)
            )
        )

        every { songDetailRepository.findByArtistAndTitle(artistString, titleString) } returns Optional.of(songEntity)
        every { artistRepository.findById(artistId) } returns Optional.of(artistEntity)
        val song = songServiceV2.findByArtistAndTitle(artistString, titleString)

        val expectedSong =
            SongDetail(
                artist = artistString,
                title = titleString,
                name = nameString,
                artistImageHeight = 0,
                artistImageWidth = 0,
                photos = listOf(
                    Photo(photoUrl, photoAttribution)
                )
            )
        assertThat(song).isEqualTo(expectedSong)
    }
}
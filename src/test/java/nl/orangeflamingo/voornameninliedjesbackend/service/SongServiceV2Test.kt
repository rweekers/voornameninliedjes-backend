package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongDetailRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepositoryV2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import java.util.*

class SongServiceV2Test {

    private val songRepositoryV2 = mock(SongRepositoryV2::class.java)
    private val songDetailRepository = mock(SongDetailRepository::class.java)
    private val artistRepository = mock(ArtistRepository::class.java)

    private val songServiceV2: SongServiceV2 = SongServiceV2(
        songRepositoryV2,
        songDetailRepository,
        artistRepository
    )

    @Test
    fun `get all songs`() {
        val song = mock(SongWithArtist::class.java)
        val status = SongStatus.SHOW
        val name = "Al"
        val limit = 10
        val page = 3
        whenever(songRepositoryV2.findAllSongsWithArtistsStartingWith(status.code, name, limit, page))
            .thenReturn(listOf(song))
        val songs = songServiceV2.findByNameStartingWith(name, status, Pageable.ofSize(limit).withPage(page))
        assertThat(songs).isEqualTo(listOf(song))
        verify(songRepositoryV2).findAllSongsWithArtistsStartingWith(status = "SHOW", name = "Al", limit = 10, offset = page)
    }

    @Test
    fun `find by artist and title`() {
        val artist = "Paul Simon"
        val title = "You Can Call Me Al"
        val name = "Al"
        val photoUrl = "https://someUrl"
        val photoAttribution = "Some attribution"
        val songEntity = mock(Song::class.java)
        val artistEntity = mock(Artist::class.java)
        val songPhoto = mock(SongPhoto::class.java)
        val artistAggregate: AggregateReference<Artist, Long> = mock()
        val artistId = 1L
        whenever(songEntity.title).thenReturn(title)
        whenever(songEntity.name).thenReturn(name)
        whenever(songPhoto.url).thenReturn(photoUrl)
        whenever(songPhoto.attribution).thenReturn(photoAttribution)
        whenever(songEntity.photos).thenReturn(mutableSetOf(songPhoto))
        whenever(artistEntity.name).thenReturn(artist)
        whenever(songEntity.artist).thenReturn(artistAggregate)
        whenever(artistAggregate.id).thenReturn(artistId)
        whenever(songDetailRepository.findByArtistAndTitle(artist, title)).thenReturn(Optional.of(songEntity))
        whenever(artistRepository.findById(artistAggregate.id ?: throw IllegalStateException())).thenReturn(Optional.of(artistEntity))
        val song = songServiceV2.findByArtistAndTitle(artist, title)

        val expectedSong =
            SongDetail(
                artist = artist,
                title = title,
                name = name,
                artistImageWidth = 0,
                artistImageHeight = 0,
                photos = listOf(
                    Photo(photoUrl, photoAttribution)
                )
            )
        assertThat(song).isEqualTo(expectedSong)
    }
}
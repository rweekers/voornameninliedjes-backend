package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistRef
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmArtist
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrack
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import reactor.core.publisher.Mono
import java.util.Optional

class LastFmEnrichmentServiceTest {

    private val mockSongRepository = mock(SongRepository::class.java)
    private val mockArtistRepository = mock(ArtistRepository::class.java)
    private val mockLastFmApiClient = mock(LastFmApiClient::class.java)
    private val lastFmEnrichmentService = LastFmEnrichmentService(
        mockSongRepository,
        mockArtistRepository,
        mockLastFmApiClient
    )
    private val song = Song(
        id = 1,
        title = "Roxanne",
        name = "Roxanne",
        artists = mutableSetOf(ArtistRef(100)),
        status = SongStatus.SHOW
    )

    @BeforeEach
    fun init() {
        `when`(mockSongRepository.findAllByStatusAndLastFmUrlIsNullOrderedByNameAndTitle(SongStatus.SHOW.code)).thenReturn(
            listOf(song)
        )
        `when`(mockArtistRepository.findById(100)).thenReturn(
            Optional.of(
                Artist(
                    id = 100,
                    name = "The Police"
                )
            )
        )
        `when`(mockLastFmApiClient.getTrack("The Police", "Roxanne")).thenReturn(
            Mono.just(
                LastFmTrack(
                    name = "Roxanne",
                    mbid = "8c2ead25-9d14-437b-aad2-cbc88958bf76",
                    url = "https://www.last.fm/music/The+Police/_/Roxanne",
                    artist = LastFmArtist(
                        name = "The Police",
                        mbid = "9e0e2b01-41db-4008-bd8b-988977d6019a",
                        url = "https://www.last.fm/music/The+Police"
                    ),
                    album = null,
                    tags = listOf(
                        LastFmTag(
                            name = "rock",
                            url = "https://www.last.fm/tag/rock"
                        ),
                        LastFmTag(
                            name = "classic rock",
                            url = "https://www.last.fm/tag/classic+rock"
                        ),
                        LastFmTag(
                            name = "80s",
                            url = "https://www.last.fm/tag/80s"
                        )
                    ),
                    wiki = null
                )
            )
        )
    }

    @Test
    fun `test enrich last fm data`() {
        lastFmEnrichmentService.enrichLastFmInfoForSongs()
        verify(mockSongRepository).findAllByStatusAndLastFmUrlIsNullOrderedByNameAndTitle("SHOW")
        verify(mockSongRepository).save(
            song.copy(
                lastFmTags = mutableSetOf(
                    SongLastFmTag(
                        name = "rock",
                        url = "https://www.last.fm/tag/rock"
                    ),
                    SongLastFmTag(
                        name = "classic rock",
                        url = "https://www.last.fm/tag/classic+rock"
                    ),
                    SongLastFmTag(
                        name = "80s",
                        url = "https://www.last.fm/tag/80s"
                    )
                )
            )
        )
    }
}
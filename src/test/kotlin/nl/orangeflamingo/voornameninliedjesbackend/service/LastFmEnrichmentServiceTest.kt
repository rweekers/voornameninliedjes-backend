package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
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
import org.springframework.data.jdbc.core.mapping.AggregateReference
import reactor.core.publisher.Mono

class LastFmEnrichmentServiceTest {

    private val mockSongRepository = mockk<SongRepository>()
    private val mockArtistRepository = mockk<ArtistRepository>()
    private val mockLastFmApiClient = mockk<LastFmApiClient>()
    private val lastFmEnrichmentService = LastFmEnrichmentService(
        mockSongRepository,
        mockArtistRepository,
        mockLastFmApiClient
    )
    private val song = Song(
        id = 1,
        title = "Roxanne",
        name = "Roxanne",
        artist = AggregateReference.to(100),
        status = SongStatus.SHOW
    )

    @BeforeEach
    fun init() {
        every { mockSongRepository.findAllByStatusAndLastFmUrlIsNullOrderedByNameAndTitle(SongStatus.SHOW.code) } returns
                listOf(song)
        every { mockArtistRepository.findById(100) } returns
                Optional.of(
                    Artist(
                        id = 100,
                        name = "The Police"
                    )
                )
        every { mockLastFmApiClient.getTrack("The Police", "Roxanne") } returns
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
    }

    @Test
    fun `test enrich last fm data`() {
        lastFmEnrichmentService.enrichLastFmInfoForSongs()
        verify { mockSongRepository.findAllByStatusAndLastFmUrlIsNullOrderedByNameAndTitle("SHOW") }
        verify {
            mockSongRepository.save(
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
}
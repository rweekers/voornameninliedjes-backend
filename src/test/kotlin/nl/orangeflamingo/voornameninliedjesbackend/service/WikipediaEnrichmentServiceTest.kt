package nl.orangeflamingo.voornameninliedjesbackend.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.jdbc.core.mapping.AggregateReference
import reactor.core.publisher.Mono

class WikipediaEnrichmentServiceTest {

    private val mockSongRepository = mockk<SongRepository>()
    private val mockWikipediaApiClient = mockk<WikipediaApiClient>()
    private val wikipediaEnrichmentService = WikipediaEnrichmentService(
        mockSongRepository,
        mockWikipediaApiClient
    )
    private val song = Song(
        id = 1,
        title = "Roxanne",
        name = "Roxanne",
        wikipediaPage = "Wiki page Roxanne",
        artist = AggregateReference.to(100),
        status = SongStatus.SHOW
    )

    @BeforeEach
    fun init() {
        every {
            mockSongRepository.findAllByStatusAndWikipediaPageIsNotNullAndWikiContentNlIsNullOrderedByNameAndTitle(
                SongStatus.SHOW.code
            )
        } returns
                listOf(song)
        every { mockWikipediaApiClient.getBackground("Wiki page Roxanne") } returns
                Mono.just(
                    WikipediaApi(
                        background = "Background on Roxanne"
                    )
                )
    }

    @Test
    fun `test enrich wikipedia data`() {
        wikipediaEnrichmentService.enrichWikipediaForSongs()
        verify { mockSongRepository.findAllByStatusAndWikipediaPageIsNotNullAndWikiContentNlIsNullOrderedByNameAndTitle("SHOW") }
        verify {
            mockSongRepository.save(
                song.copy(
                    wikiContentNl = "Background on Roxanne"
                )
            )
        }
    }
}
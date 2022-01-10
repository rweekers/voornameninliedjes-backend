package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistRef
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import reactor.core.publisher.Mono

class WikipediaEnrichmentServiceTest {

    private val mockSongRepository = mock(SongRepository::class.java)
    private val mockWikipediaApiClient = mock(WikipediaApiClient::class.java)
    private val wikipediaEnrichmentService = WikipediaEnrichmentService(
        mockSongRepository,
        mockWikipediaApiClient
    )
    private val song = Song(
        id = 1,
        title = "Roxanne",
        name = "Roxanne",
        wikipediaPage = "Wiki page Roxanne",
        artists = mutableSetOf(ArtistRef(100)),
        status = SongStatus.SHOW
    )

    @BeforeEach
    fun init() {
        `when`(mockSongRepository.findAllByStatusAndWikipediaPageIsNotNullAndWikiContentNlIsNullOrderedByNameAndTitle(SongStatus.SHOW.code)).thenReturn(
            listOf(song)
        )
        `when`(mockWikipediaApiClient.getBackground("Wiki page Roxanne")).thenReturn(
            Mono.just(
                WikipediaApi(
                    background = "Background on Roxanne"
                )
            )
        )
    }

    @Test
    fun `test enrich wikipedia data`() {
        wikipediaEnrichmentService.enrichWikipediaForSongs()
        verify(mockSongRepository).findAllByStatusAndWikipediaPageIsNotNullAndWikiContentNlIsNullOrderedByNameAndTitle("SHOW")
        verify(mockSongRepository).save(
            song.copy(
                wikiContentNl = "Background on Roxanne"
            )
        )
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.PageDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.QueryDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaSongDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class WikipediaHttpApiClientTest {

    private val mockWebClient = mock(WebClient::class.java)
    private val mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec::class.java)
    private val mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec::class.java)
    private val mockResponseSpec = mock(WebClient.ResponseSpec::class.java)

    private val wikipediaResponse = Mono.just(
        WikipediaSongDto(
            batchcomplete = "true",
            query = QueryDto(
                pages = listOf(
                    PageDto(
                        pageid = 1,
                        title = "theTitle",
                        extract = "Some background from wikipedia"
                    )
                )
            )
        )
    )

    private val wikipediaHttpApiClient = WikipediaHttpApiClient(
        mockWebClient
    )

    @BeforeEach
    fun init() {
        `when`(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec)
        `when`(mockRequestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(mockRequestHeadersSpec)
        `when`(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec)
        `when`(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        `when`(mockResponseSpec.bodyToMono(WikipediaSongDto::class.java)).thenReturn(wikipediaResponse)
    }

    @Test
    fun `get background`() {
        val requestWikiPage = "some_song"
        val apiResponse = wikipediaHttpApiClient.getBackground(requestWikiPage).block()
        assertEquals(
            "Some background from wikipedia\n\n[https://nl.wikipedia.org/wiki/$requestWikiPage](https://nl.wikipedia.org/wiki/$requestWikiPage)",
            apiResponse?.background
        )
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.client

import io.mockk.every
import io.mockk.mockk
import nl.orangeflamingo.voornameninliedjesbackend.domain.PageDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.QueryDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaSongDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class WikipediaHttpApiClientTest {

    private val mockWebClient = mockk<WebClient>()
    private val mockRequestHeadersUriSpec = mockk<WebClient.RequestHeadersUriSpec<*>>()
    private val mockRequestHeadersSpec = mockk<WebClient.RequestHeadersSpec<*>>()
    private val mockResponseSpec = mockk<WebClient.ResponseSpec>()

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
        every { mockWebClient.get() } returns mockRequestHeadersUriSpec
        every { mockRequestHeadersUriSpec.uri(any(String::class), any(String::class)) } returns mockRequestHeadersSpec
        every { mockRequestHeadersSpec.retrieve() } returns mockResponseSpec
        every { mockResponseSpec.bodyToMono(WikipediaSongDto::class.java) } returns wikipediaResponse
    }

    @Test
    fun `get background`() {
        val requestWikiPage = "some_song"
        val apiResponse = wikipediaHttpApiClient.getBackground(requestWikiPage).block()
        assertEquals(
            "Some background from wikipedia",
            apiResponse?.background
        )
    }
}
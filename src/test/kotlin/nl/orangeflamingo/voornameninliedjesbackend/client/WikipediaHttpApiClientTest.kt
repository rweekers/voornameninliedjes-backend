package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import reactor.test.StepVerifier

class WikipediaHttpApiClientTest {

    private val mockWebServer = MockWebServer()
    private lateinit var client: WikipediaHttpApiClient

    @BeforeEach
    fun init() {
        mockWebServer.start()
        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()
        client = WikipediaHttpApiClient(restClient)
    }

    @Test
    fun `get background`() {
        val json = """
            {
                "batchcomplete": true,
                "query": {
                    "pages": [
                        {
                            "pageid": 4599996,
                            "ns": 0,
                            "title": "Roxanne",
                            "extract": "Roxanne is een nummer van de Britse band The Police uit april 1978."
                        }
                    ]
                }
            }
        """

        mockWebServer.enqueue(
            MockResponse()
            .setBody(json)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200)
        )

        client.getBackground("Roxanne")

        /*StepVerifier.create(client.getBackground("Roxanne"))
            .assertNext { result ->
                when (result) {
                    is WikipediaApi -> {
                        assertThat(result.background)
                            .isEqualTo("Roxanne is een nummer van de Britse band The Police uit april 1978.")
                    }
                }
            }
            .verifyComplete()*/
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
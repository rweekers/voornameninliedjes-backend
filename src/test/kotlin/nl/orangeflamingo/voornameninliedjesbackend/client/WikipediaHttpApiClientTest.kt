package nl.orangeflamingo.voornameninliedjesbackend.client

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient

class WikipediaHttpApiClientTest {

    private val mockWebServer = MockWebServer()
    private lateinit var client: WikipediaHttpApiClient

    @BeforeEach
    fun init() {
        mockWebServer.start()
        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
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

        client.getBackground("nl", "Roxanne")
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
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
        client = WikipediaHttpApiClient(
            mockWebServer.url("/").toString(),
            RestClient.builder()
        )
    }

    @Test
    fun `get background`() {
        val json = """
            {
                "type": "standard",
                "title": "Roxanne",
                "extract": "Roxanne is een nummer van de Britse band The Police uit april 1978.",
                "extract_html": "<p>Roxanne is een nummer van de Britse band The Police uit april 1978.</p>"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
            .setBody(json)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200)
        )

        val result = client.getBackground("Roxanne")

        assertThat(result).isPresent
        assertThat(result.get()).isInstanceOf(WikipediaApi::class.java)
        assertThat(result.get().background).isEqualTo("Roxanne is een nummer van de Britse band The Police uit april 1978.")

        val request = mockWebServer.takeRequest()
        assertThat(request.path).contains("/api/rest_v1/page/summary/Roxanne")

        val userAgent = request.headers["User-Agent"]
        assertThat(userAgent).isNotNull
        assertThat(userAgent).contains("VoornamenInLiedjesNederland/1.0")
        assertThat(userAgent).contains("info@voornameninliedjes.nl")
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
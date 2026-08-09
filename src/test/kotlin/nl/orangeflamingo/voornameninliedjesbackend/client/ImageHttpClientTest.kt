package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient

class ImageHttpClientTest {

    private val mockWebServer = MockWebServer()
    private lateinit var client: ImageHttpClient

    @BeforeEach
    fun setUp() {
        mockWebServer.start()
        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()
        client = ImageHttpClient(restClient)
    }

    @Test
    fun `create image blur`() {
        val json = """
            {
                "name": "The_Police_Roxanne.jpg",
                "hash": "f06f8395e3a74e5fac609ac69cbf1515"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
        )

        val result = client.createImageBlur("Roxanne", 100, 100)

        assertThat(result).isPresent
        assertThat(result.get()).isInstanceOf(ImageHashDto::class.java)
        assertThat(result.get().name).isEqualTo("The_Police_Roxanne.jpg")
        assertThat(result.get().hash).isEqualTo("f06f8395e3a74e5fac609ac69cbf1515")
    }

    @Test
    fun `get dimensions`() {
        val json = """
            {
                "name": "The_Police_Roxanne.jpg",
                "width": 10,
                "height": 10
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200)
        )

        val result = client.getDimensions("https://upload.wikimedia.org/wikipedia/commons/a/a2/ThePolice_2007.jpg")

        assertThat(result).isPresent
        assertThat(result.get()).isInstanceOf(ImageDimensionsDto::class.java)
        assertThat(result.get().name).isEqualTo("The_Police_Roxanne.jpg")
        assertThat(result.get().width).isEqualTo(10)
        assertThat(result.get().height).isEqualTo(10)
    }

    @Test
    fun `download image`() {
        val responseBody = "/var/www/images/The_Polace_Roxanne.jpg"

        mockWebServer.enqueue(
            MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "text/plain")
                .setResponseCode(200)
        )

        val result = client.downloadImage(
            "https://upload.wikimedia.org/wikipedia/commons/a/a2/ThePolice_2007.jpg",
            "test-image.jpg",
            true
        )

        assertThat(result).isPresent
        assertThat(result.get()).isEqualTo(responseBody)

        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.method).isEqualTo("POST")
        assertThat(recordedRequest.path).contains("url=")
        assertThat(recordedRequest.path).contains("filename=test-image.jpg")
        assertThat(recordedRequest.path).contains("overwrite=true")
    }
}
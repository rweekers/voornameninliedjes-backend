package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiLicenseDetailDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiLicenseDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiLicensesDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiOwnerDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiOwnerIdDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiPersonDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiPhotoDetailDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiPhotoDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiPhotoTitleDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiPhotosurlDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiUsernameDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class FlickrHttpApiClientTest {

    private val mockWebClient = mock(WebClient::class.java)
    private val mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec::class.java)
    private val mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec::class.java)
    private val mockResponseSpec = mock(WebClient.ResponseSpec::class.java)

    private val flickrPhotoResponse = Mono.just(
        FlickrApiPhotoDto(
            photo = FlickrApiPhotoDetailDto(
                farm = "farm",
                server = "server",
                id = "1",
                secret = "secret",
                license = "license",
                owner = FlickrApiOwnerIdDto(nsid = "nsid"),
                title = FlickrApiPhotoTitleDto(content = "content")
            )
        )
    )
    private val flickrOwnerResponse = Mono.just(
        FlickrApiOwnerDto(
            person = FlickrApiPersonDto(
                id = "ownerId",
                username = FlickrApiUsernameDto("ownerUsername"),
                photosurl = FlickrApiPhotosurlDto("ownerPhotoUrl")
            )
        )
    )
    private val flickrApiLicenseResponse = Mono.just(
        FlickrApiLicenseDto(
            licenses = FlickrApiLicensesDto(
                license = listOf(
                    FlickrApiLicenseDetailDto(
                        id = "licenseId",
                        name = "licenseName",
                        url = "licenseUrl"
                    )
                )
            )
        )
    )

    private val flickrHttpApiClient = FlickrHttpApiClient(
        mockWebClient
    )

    @BeforeEach
    fun init() {
        `when`(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec)
        `when`(mockRequestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(mockRequestHeadersSpec)
        `when`(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec)
        `when`(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        `when`(mockResponseSpec.bodyToMono(FlickrApiPhotoDto::class.java)).thenReturn(flickrPhotoResponse)
        `when`(mockResponseSpec.bodyToMono(FlickrApiOwnerDto::class.java)).thenReturn(flickrOwnerResponse)
        `when`(mockResponseSpec.bodyToMono(FlickrApiLicenseDto::class.java)).thenReturn(flickrApiLicenseResponse)
    }

    @Test
    fun `get photos`() {
        val apiResponse = flickrHttpApiClient.getPhoto("1").block()
        assertEquals("https://farmfarm.staticflickr.com/server/1_secret_c.jpg", apiResponse?.url)
    }

    @Test
    fun `get owner`() {
        val apiResponse = flickrHttpApiClient.getOwnerInformation("1").block()
        assertEquals("ownerUsername", apiResponse?.username)
    }

    @Test
    fun `get licenses`() {
        val apiResponse = flickrHttpApiClient.getLicenses().block()
        assertEquals("licenseName", apiResponse?.license?.first()?.name)
    }
}
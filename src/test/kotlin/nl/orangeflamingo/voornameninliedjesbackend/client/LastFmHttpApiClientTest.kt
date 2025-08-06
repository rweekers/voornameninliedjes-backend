package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmAlbumDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmError
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmResponseDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTopTagsDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrack
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrackDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmWikiDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class LastFmHttpApiClientTest {

    private val mockWebClient = mock(WebClient::class.java)
    private val mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec::class.java)
    private val mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec::class.java)
    private val mockResponseSpec = mock(WebClient.ResponseSpec::class.java)

    private val mockErrorRequestHeadersSpec = mock(WebClient.RequestHeadersSpec::class.java)
    private val mockErrorResponseSpec = mock(WebClient.ResponseSpec::class.java)

    private val lastFmResponse: Mono<LastFmResponseDto> = Mono.just(
        LastFmResponseDto(
            track = LastFmTrackDto(
                name = "Roxanne",
                url = "https://www.last.fm/music/The+Police/_/Roxanne",
                mbid = "8c2ead25-9d14-437b-aad2-cbc88958bf76",
                artist = LastFmArtistDto(
                    name = "The Police",
                    url = "https://www.last.fm/music/The+Police",
                    mbid = "9e0e2b01-41db-4008-bd8b-988977d6019a",
                ),
                album = LastFmAlbumDto(
                    artist = "The Police",
                    title = "Outlandos D'Amour",
                    mbid = "8be3709f-25f8-4314-a39a-6df38798c35b",
                    url = "https://www.last.fm/music/The+Police/Outlandos+D%27Amour"
                ),
                toptags = LastFmTopTagsDto(
                    tag = listOf(
                        LastFmTagDto(
                            name = "rock",
                            url = "https://www.last.fm/tag/rock"
                        ),
                        LastFmTagDto(
                            name = "classic rock",
                            url = "https://www.last.fm/tag/classic+rock"
                        ),
                        LastFmTagDto(
                            name = "80s",
                            url = "https://www.last.fm/tag/80s"
                        )
                    )
                ),
                wiki = LastFmWikiDto(
                    summary = "Summary...",
                    content = "Content..."
                )
            )
        )
    )
    private val lastFmErrorResponse: Mono<LastFmResponseDto> = Mono.just(
        LastFmResponseDto(
            error = "6",
            message = "Track not found",
            track = null
        )
    )

    private val lastFmHttpApiClient = LastFmHttpApiClient(
        mockWebClient, "some-api-key"
    )

    @BeforeEach
    fun init() {
        `when`(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec)
        `when`(mockRequestHeadersUriSpec.uri(anyString(), eq("The Police"), eq("Roxanne"))).thenReturn(mockRequestHeadersSpec)
        `when`(mockRequestHeadersUriSpec.uri(anyString(), eq("The Police"), eq("Not actually Roxanne"))).thenReturn(mockErrorRequestHeadersSpec)
        `when`(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        `when`(mockResponseSpec.bodyToMono(LastFmResponseDto::class.java)).thenReturn(lastFmResponse)
        `when`(mockErrorRequestHeadersSpec.retrieve()).thenReturn(mockErrorResponseSpec)
        `when`(mockErrorResponseSpec.bodyToMono(LastFmResponseDto::class.java)).thenReturn(lastFmErrorResponse)
    }

    @Test
    fun `get track info`() {
        val apiResponse = lastFmHttpApiClient.getTrack("The Police", "Roxanne").block()
        assertThat(apiResponse).isInstanceOf(LastFmTrack::class.java)
        val apiResponseTrack = apiResponse as LastFmTrack
        assertEquals("https://www.last.fm/music/The+Police/_/Roxanne", apiResponseTrack.url)
    }

    @Test
    fun `track info not found`() {
        val apiResponse = lastFmHttpApiClient.getTrack("The Police", "Not actually Roxanne").block()
        assertThat(apiResponse).isInstanceOf(LastFmError::class.java)
        val apiResponseTrack = apiResponse as LastFmError
        assertEquals("6", apiResponseTrack.code)
    }
}
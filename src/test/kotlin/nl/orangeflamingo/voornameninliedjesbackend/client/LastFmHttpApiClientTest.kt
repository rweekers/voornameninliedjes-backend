package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmError
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrack
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

class LastFmHttpApiClientTest {

    private val mockWebServer = MockWebServer()
    private lateinit var client: LastFmHttpApiClient

    @BeforeEach
    fun setUp() {
        mockWebServer.start()
        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()
        client = LastFmHttpApiClient(webClient, "key")
    }

    @Test
    fun `should parse regular response correctly`() {
        val json =
            """{"track":{"name":"Roxanne","url":"https://www.last.fm/music/The+Police/_/Roxanne","duration":"89000","streamable":{"#text":"0","fulltrack":"0"},"listeners":"1669955","playcount":"11291064","artist":{"name":"The Police","mbid":"9e0e2b01-41db-4008-bd8b-988977d6019a","url":"https://www.last.fm/music/The+Police"},"album":{"artist":"Sting","title":"The Very Best of Sting and The Police","url":"https://www.last.fm/music/Sting/The+Very+Best+of+Sting+and+The+Police","image":[{"#text":"https://lastfm.freetls.fastly.net/i/u/34s/1f7bd956c159483fc8d96511c5fc2cfb.png","size":"small"},{"#text":"https://lastfm.freetls.fastly.net/i/u/64s/1f7bd956c159483fc8d96511c5fc2cfb.png","size":"medium"},{"#text":"https://lastfm.freetls.fastly.net/i/u/174s/1f7bd956c159483fc8d96511c5fc2cfb.png","size":"large"},{"#text":"https://lastfm.freetls.fastly.net/i/u/300x300/1f7bd956c159483fc8d96511c5fc2cfb.png","size":"extralarge"}]},"toptags":{"tag":[{"name":"rock","url":"https://www.last.fm/tag/rock"},{"name":"classic rock","url":"https://www.last.fm/tag/classic+rock"},{"name":"80s","url":"https://www.last.fm/tag/80s"},{"name":"new wave","url":"https://www.last.fm/tag/new+wave"},{"name":"The Police","url":"https://www.last.fm/tag/The+Police"}]},"wiki":{"published":"30 Nov 2008, 16:15","summary":"\"Roxanne\" is a hit song by the rock band The Police, first released in 1978 as a single and on their album Outlandos d'Amour. It was written about a prostitute in southern France.\n\nPolice lead singer Sting wrote the song, inspired by the prostitutes he saw near the band's seedy hotel while in Paris, France in October 1977 to perform at the Nashville Club. The title of the song comes from the name of the character in the play Cyrano de Bergerac, an old poster of which was hanging in the hotel foyer. <a href=\"http://www.last.fm/music/The+Police/_/Roxanne\">Read more on Last.fm</a>.","content":"\"Roxanne\" is a hit song by the rock band The Police, first released in 1978 as a single and on their album Outlandos d'Amour. It was written about a prostitute in southern France.\n\nPolice lead singer Sting wrote the song, inspired by the prostitutes he saw near the band's seedy hotel while in Paris, France in October 1977 to perform at the Nashville Club. The title of the song comes from the name of the character in the play Cyrano de Bergerac, an old poster of which was hanging in the hotel foyer.\n\nSting had originally conceived the song as a bossa nova, although he credits Police drummer Stewart Copeland for suggesting its final rhythmic form as a tango. During recording, Sting accidentally sat down on a piano keyboard in the studio, resulting in the atonal piano chord and laughter preserved at the beginning of the track. The Police were initially diffident about the song, but Miles Copeland III was immediately enthusiastic after hearing it, becoming their manager and getting them their first record deal with A&M Records. The single did not chart at first, but it was re-released in April 1979 and reached #12 in the UK and #32 in the U.S., and went on to become one of the classic Police songs as well as a staple of Sting's performances during his solo career. \"Roxanne\" has appeared on every single one of The Police's Greatest hits albums.\n\nRolling Stone ranked it #388 on their list of the 500 Greatest Songs of All Time.\n\nThis was also the appropriately first song the band performed live (at the 2007 Grammy Awards) to kick off their 30th Anniversary Reunion Tour. <a href=\"http://www.last.fm/music/The+Police/_/Roxanne\">Read more on Last.fm</a>. User-contributed text is available under the Creative Commons By-SA License; additional terms may apply."}}}"""
        mockWebServer.enqueue(MockResponse()
            .setBody(json)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200)
        )

        StepVerifier.create(client.getTrack("The Police", "Roxanne"))
            .assertNext { result ->
                when (result) {
                    is LastFmTrack -> {
                        assertThat(result.url)
                            .isEqualTo("https://www.last.fm/music/The+Police/_/Roxanne")
                        assertThat(result.name)
                            .isEqualTo("Roxanne")
                    }
                    is LastFmError -> fail("Expected LastFmTrack but got LastFmError: ${result.message}")
                    else -> fail("Unexpected type: ${result::class.simpleName}")
                }
            }
            .verifyComplete()
    }

    @Test
    fun `should parse error response correctly`() {
        val json = """{
            "error": 6,
            "message": "Track not found",
            "links": []
        }"""

        mockWebServer.enqueue(MockResponse()
            .setBody(json)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200)
        )

        StepVerifier.create(client.getTrack("The Police", "Roxanne"))
            .assertNext { result ->
                when (result) {
                    is LastFmError -> {
                        assertThat(result.code)
                            .isEqualTo("6")
                        assertThat(result.message)
                            .isEqualTo("Track not found")
                    }
                    is LastFmTrack -> fail("Expected LastFmError but got LastFmTrack: ${result.artist} - ${result.name}")
                    else -> fail("Unexpected type: ${result::class.simpleName}")
                }
            }
            .verifyComplete()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}

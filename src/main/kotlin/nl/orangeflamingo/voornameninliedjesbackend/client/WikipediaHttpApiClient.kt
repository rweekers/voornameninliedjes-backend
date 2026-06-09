package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import nl.orangeflamingo.voornameninliedjesbackend.dto.WikipediaApiResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient
import java.util.*

class WikipediaHttpApiClient(
    private val wikipediaRestClient: RestClient
) : WikipediaApiClient {

    override fun getBackground(wikipediaPage: String): Optional<WikipediaApi> {
        return try {
                val rawResponse = wikipediaRestClient.get()
                .uri { builder ->
                    builder.path("/w/api.php")
                        .queryParam("action", "query")
                        .queryParam("prop", "extracts")
                        .queryParam("exsentences", 10)
                        .queryParam("exlimit", 1)
                        .queryParam("titles", wikipediaPage)
                        .queryParam("explaintext", true)
                        .queryParam("formatversion", "2")
                        .queryParam("format", "json")
                        .build()
                }
                .header(
                    HttpHeaders.USER_AGENT,
                    "VoornamenInLiedjesNederland/1.0 (https://www.voornameninliedjes.nl; info@voornameninliedjes.nl)"
                )
                .retrieve()
                .body(WikipediaApiResponse::class.java)

            val extractText = rawResponse?.query?.pages?.firstOrNull()?.extract

            extractText?.takeIf { it.isNotBlank() }
                ?.let { text ->
                    Optional.of(WikipediaApi(background = text.trim()))
                } ?: Optional.empty() // Return empty if no text found

        } catch (e: Exception) {
            // Log error if needed
            Optional.empty()
        }
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import nl.orangeflamingo.voornameninliedjesbackend.dto.WikipediaApiResponse
import org.springframework.web.client.RestClient
import java.util.*

class WikipediaHttpApiClient(
    private val wikipediaRestClient: RestClient
) : WikipediaApiClient {

    override fun getBackground(wikipediaPage: String): Optional<WikipediaApi> {
        return try {
            // 1. Fetch the RAW API response
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
                .retrieve()
                .body(WikipediaApiResponse::class.java)

            // 2. Extract the text from the nested structure
            val extractText = rawResponse?.query?.pages?.firstOrNull()?.extract

            // 3. Create the DOMAIN object ONLY if text exists
            extractText?.takeIf { it.isNotBlank() }
                ?.let { text ->
                    // ✅ Correct: Creating WikipediaApi (Domain) with the text
                    Optional.of(WikipediaApi(background = text.trim()))
                } ?: Optional.empty() // Return empty if no text found

        } catch (e: Exception) {
            // Log error if needed
            Optional.empty()
        }
    }
}
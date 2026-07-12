package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import nl.orangeflamingo.voornameninliedjesbackend.dto.WikipediaSummaryResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient
import java.util.Optional

class WikipediaHttpApiClient(
    private val restClientBuilder: RestClient.Builder
) : WikipediaApiClient {

    override fun getBackground(language: String, wikipediaPage: String): Optional<WikipediaApi> {
        return try {
            val restClient = restClientBuilder
                .baseUrl("https://$language.wikipedia.org")
                .build()

            val response = restClient.get()
                .uri("/api/rest_v1/page/summary/{title}", wikipediaPage)
                .header(
                    HttpHeaders.USER_AGENT,
                    "VoornamenInLiedjesNederland/1.0 (https://www.voornameninliedjes.nl; info@voornameninliedjes.nl)"
                )
                .retrieve()
                .body(WikipediaSummaryResponse::class.java)

            response?.extract
                ?.takeIf { it.isNotBlank() }
                ?.let { Optional.of(WikipediaApi(background = it.trim())) }
                ?: Optional.empty()

        } catch (e: Exception) {
            Optional.empty()
        }
    }
}
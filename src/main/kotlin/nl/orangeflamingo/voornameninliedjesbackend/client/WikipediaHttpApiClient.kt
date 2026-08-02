package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.controller.WikipediaLanguage
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import nl.orangeflamingo.voornameninliedjesbackend.dto.WikipediaSummaryResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient
import java.util.Optional

class WikipediaHttpApiClient(
    private val restClientBuilder: RestClient.Builder
) : WikipediaApiClient {

    @Suppress("kotlinsecurity:S5144")
    // Safe: language is an enum with fixed hosts; page is validated in the controller
    // (alphanumeric, underscore, hyphen, parentheses only, max 255 chars)
    override fun getBackground(wikipediaLanguage: WikipediaLanguage, wikipediaPage: String): Optional<WikipediaApi> {
        return try {
            val restClient = restClientBuilder
                .baseUrl(wikipediaLanguage.baseUrl)
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
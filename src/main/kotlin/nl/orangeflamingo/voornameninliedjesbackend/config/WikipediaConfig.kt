package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaHttpApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

@Configuration
@Profile("!integration-test")
class WikipediaConfig {

    @Bean
    fun wikipediaRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://nl.wikipedia.org")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }


    @Bean
    fun wikipediaApiClient(wikipediaWebClient: RestClient): WikipediaApiClient {
        return WikipediaHttpApiClient(wikipediaWebClient)
    }
}
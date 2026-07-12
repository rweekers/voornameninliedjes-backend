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
    fun wikipediaRestClientBuilder(): RestClient.Builder {
        return RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    }

    @Bean
    fun wikipediaApiClient(
        wikipediaRestClientBuilder: RestClient.Builder
    ): WikipediaApiClient {
        return WikipediaHttpApiClient(wikipediaRestClientBuilder)
    }
}
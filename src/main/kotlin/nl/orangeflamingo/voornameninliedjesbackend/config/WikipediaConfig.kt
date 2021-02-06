package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaHttpApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
@Profile("!integration-test")
class WikipediaConfig {

    @Bean
    fun wikipediaWebClient(): WebClient {
        return WebClient.builder()
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create().followRedirect(true)
                )
            )
            .baseUrl("https://nl.wikipedia.org")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun wikipediaApiClient(wikipediaWebClient: WebClient): WikipediaApiClient {
        return WikipediaHttpApiClient(wikipediaWebClient)
    }
}
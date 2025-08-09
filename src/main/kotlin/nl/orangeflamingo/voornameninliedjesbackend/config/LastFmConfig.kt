package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmHttpApiClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@Profile("!integration-test")
class LastFmConfig(
    @Value("\${voornameninliedjes.lastfm.api.key}") private val lastFmKey: String
) {

    @Bean
    fun lastFmWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://ws.audioscrobbler.com/2.0")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun lastFmApiClient(lastFmWebClient: WebClient): LastFmApiClient {
        return LastFmHttpApiClient(lastFmWebClient, lastFmKey)
    }
}
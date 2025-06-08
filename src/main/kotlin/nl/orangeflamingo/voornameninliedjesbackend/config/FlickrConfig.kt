package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrHttpApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@Profile("!integration-test")
class FlickrConfig {

    @Bean
    fun flickrWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://api.flickr.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun flickrApiClient(flickrWebClient: WebClient): FlickrApiClient {
        return FlickrHttpApiClient(flickrWebClient)
    }
}
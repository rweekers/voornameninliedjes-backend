package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.ImageHttpApiClient
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
class ImageConfig {

    @Bean
    fun imageWebClient(): WebClient {
        return WebClient.builder()
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create().followRedirect(true)
                )
            )
            .baseUrl("https://images.voornameninliedjes.nl")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun imageApiClient(imageWebClient: WebClient): ImageApiClient {
        return ImageHttpApiClient(imageWebClient)
    }
}
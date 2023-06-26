package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.ImageHttpApiClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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

    private val log = LoggerFactory.getLogger(ImageConfig::class.java)

    @Value("\${voornameninliedjes.images.service.path}")
    private val imagesServicePath: String = "https://images.voornameninliedjes.nl"

    @Bean
    fun imageWebClient(): WebClient {
        log.info("Gotten path $imagesServicePath")
        return WebClient.builder()
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create().followRedirect(true)
                )
            )
            .baseUrl(imagesServicePath)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun imageApiClient(imageWebClient: WebClient): ImageApiClient {
        return ImageHttpApiClient(imageWebClient)
    }
}
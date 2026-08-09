package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import nl.orangeflamingo.voornameninliedjesbackend.client.ImageHttpClient
import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmHttpApiClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
@Profile("!integration-test")
class ImageConfig {

    @Value("\${voornameninliedjes.images.service.path}")
    private val imagesServicePath: String = "https://images.voornameninliedjes.nl"

    @Bean
    fun imageRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl(imagesServicePath)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun imageClient(imageRestClient: RestClient): ImageClient {
        return ImageHttpClient(imageRestClient)
    }
}

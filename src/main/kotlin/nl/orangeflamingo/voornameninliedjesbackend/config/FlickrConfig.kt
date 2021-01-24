package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrHttpApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!integration-test")
class FlickrConfig {

    @Bean
    fun flickrApiClient(): FlickrApiClient {
        return FlickrHttpApiClient()
    }
}
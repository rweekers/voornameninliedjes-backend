package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrHttpApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class IntegrationTestConfiguration {

    @Bean
    @Primary
    fun flickrApiClient(): FlickrApiClient {
        return FakeFlickrApiClient()
    }
}
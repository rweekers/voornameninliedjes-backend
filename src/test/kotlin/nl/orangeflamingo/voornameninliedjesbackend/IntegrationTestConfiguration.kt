package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.client.FakeFlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FakeWikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles

@Configuration
@Profile("!smoke-test")
class IntegrationTestConfiguration {

    @Bean
    @Primary
    fun flickrApiClient(): FlickrApiClient {
        return FakeFlickrApiClient()
    }

    @Bean
    @Primary
    fun wikipediaApiClient(): WikipediaApiClient {
        return FakeWikipediaApiClient()
    }
}
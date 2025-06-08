package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.client.FakeFlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FakeImageClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FakeLastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FakeWikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

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

    @Bean
    @Primary
    fun lastFmApiClient(): LastFmApiClient {
        return FakeLastFmApiClient()
    }

    @Bean
    @Primary
    fun imageClient(): ImageClient {
        return FakeImageClient()
    }
}
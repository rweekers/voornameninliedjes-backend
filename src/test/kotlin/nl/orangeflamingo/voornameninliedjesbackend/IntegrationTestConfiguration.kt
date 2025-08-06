package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.client.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!smoke-test")
class IntegrationTestConfiguration {

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
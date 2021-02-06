package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import reactor.core.publisher.Mono

class FakeWikipediaApiClient : WikipediaApiClient {
    override fun getBackground(wikipediaPage: String): Mono<WikipediaApi> {
        return Mono.just(
            WikipediaApi(background = "Mooie background uit fake wikipedia client")
        )
    }
}
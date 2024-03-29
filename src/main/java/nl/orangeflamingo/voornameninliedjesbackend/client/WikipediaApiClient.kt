package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import reactor.core.publisher.Mono

fun interface WikipediaApiClient {

    fun getBackground(wikipediaPage: String): Mono<WikipediaApi>
}
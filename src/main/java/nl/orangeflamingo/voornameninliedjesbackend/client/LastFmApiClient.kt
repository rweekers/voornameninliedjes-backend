package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmResponse
import reactor.core.publisher.Mono

fun interface LastFmApiClient {

    fun getTrack(artist: String, title: String): Mono<LastFmResponse>
}
package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrack
import reactor.core.publisher.Mono

interface LastFmApiClient {

    fun getTrack(artist: String, title: String): Mono<LastFmTrack>
}
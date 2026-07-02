package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmResponse
import java.util.Optional

fun interface LastFmApiClient {

    fun getTrack(artist: String, title: String): Optional<LastFmResponse>
}
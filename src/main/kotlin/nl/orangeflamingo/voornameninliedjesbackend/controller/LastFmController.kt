package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmResponse
import nl.orangeflamingo.voornameninliedjesbackend.utils.clean
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api")
@Profile("dev")
class LastFmController(
    private val lastFmApiClient: LastFmApiClient
) {

    private val log = LoggerFactory.getLogger(LastFmController::class.java)

    @GetMapping("/lastfm/{artist}/{title}")
    fun getLastFmInfoByArtistAndTitle(@PathVariable artist: String, @PathVariable title: String): Mono<LastFmResponse> {
        log.info(
            "Getting last fm information for ${
                artist.replace(
                    "[\n\r]".toRegex(),
                    "_"
                )
            } - ${title.replace("[\n\r]".toRegex(), "_")}"
        )
        return lastFmApiClient.getTrack(
            artist.replace("'", ""),
            title.clean()
        )
    }
}
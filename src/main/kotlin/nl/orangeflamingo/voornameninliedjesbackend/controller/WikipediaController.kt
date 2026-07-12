package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RestController
@RequestMapping("/api")
@Profile("dev")
class WikipediaController(
    private val wikipediaApi: WikipediaApiClient
) {

    private val log = LoggerFactory.getLogger(WikipediaController::class.java)

    @GetMapping("/wikipedia/{language}/{page}")
    fun getLastFmInfoByArtistAndTitle(
        @PathVariable language: String,
        @PathVariable page: String
    ): Optional<WikipediaApi> {
        log.info("Getting wikipedia for page ${page.replace("[\n\r]".toRegex(), "_")}")
        return wikipediaApi.getBackground(language,page)
    }
}
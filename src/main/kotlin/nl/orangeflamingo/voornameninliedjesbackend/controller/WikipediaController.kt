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

    private val VALID_PAGE_REGEX = Regex("^[a-zA-Z0-9_\\-()]+$")
    private val MAX_TITLE_LENGTH = 255

    private val log = LoggerFactory.getLogger(WikipediaController::class.java)

    @Suppress("kotlinsecurity:S5144")
    // Safe: language is an enum with fixed hosts; page is validated in the controller
    // (alphanumeric, underscore, hyphen, parentheses only, max 255 chars)
    @GetMapping("/wikipedia/{language}/{page}")
    fun getLastFmInfoByArtistAndTitle(
        @PathVariable language: WikipediaLanguage,
        @PathVariable page: String
    ): Optional<WikipediaApi> {
        log.info("Getting wikipedia for page ${page.replace("[\n\r]".toRegex(), "_")}")
        return wikipediaApi.getBackground(language,normalizeAndValidate(page))
    }

    private fun normalizeAndValidate(page: String): String {
        val decoded = java.net.URLDecoder.decode(page, Charsets.UTF_8)

        validateWikipediaPage(decoded)

        return decoded.take(MAX_TITLE_LENGTH)
    }

    private fun validateWikipediaPage(page: String) {
        require('\u0000' !in page) { "Page name cannot contain null bytes" }

        require(!page.contains("..")) { "Page name cannot contain '..'" }
        require(!page.startsWith("/")) { "Page name cannot start with '/'" }

        require(page.length <= MAX_TITLE_LENGTH) {
            "Page name too long (max $MAX_TITLE_LENGTH characters)"
        }

        require(VALID_PAGE_REGEX.matches(page)) {
            "Page name contains invalid characters. Allowed: letters, numbers, underscore, hyphen, parentheses"
        }
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.controller.WikipediaLanguage
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import java.util.Optional

class FakeWikipediaApiClient : WikipediaApiClient {
    override fun getBackground(wikipediaLanguage: WikipediaLanguage, wikipediaPage: String): Optional<WikipediaApi> {
        return Optional.of(
            WikipediaApi(background = "Mooie background uit fake wikipedia client")
        )
    }
}
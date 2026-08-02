package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.controller.WikipediaLanguage
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import java.util.*

fun interface WikipediaApiClient {

    fun getBackground(wikipediaLanguage: WikipediaLanguage, wikipediaPage: String): Optional<WikipediaApi>
}
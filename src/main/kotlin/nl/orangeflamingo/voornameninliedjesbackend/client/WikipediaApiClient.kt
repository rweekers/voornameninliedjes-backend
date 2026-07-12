package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import java.util.*

fun interface WikipediaApiClient {

    fun getBackground(language: String, wikipediaPage: String): Optional<WikipediaApi>
}
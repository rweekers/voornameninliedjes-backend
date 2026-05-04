package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import java.util.*

fun interface WikipediaApiClient {

    fun getBackground(wikipediaPage: String): Optional<WikipediaApi>
}
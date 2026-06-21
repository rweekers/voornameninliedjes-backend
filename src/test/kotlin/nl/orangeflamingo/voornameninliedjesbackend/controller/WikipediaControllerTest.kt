package nl.orangeflamingo.voornameninliedjesbackend.controller

import io.mockk.every
import io.mockk.mockk
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class WikipediaControllerTest {

    private val wikipediaApi = mockk<WikipediaApiClient>()

    private val wikipediaController = WikipediaController(wikipediaApi)

    @Test
    fun `get wikipedia by page`() {
        val page = "Roxanne"
        val wikipageRoxanne = WikipediaApi("Some background on Roxanne")
        every { wikipediaApi.getBackground(page) } returns Optional.of(wikipageRoxanne)
        val wikipageFound = wikipediaController.getLastFmInfoByArtistAndTitle(page)
        assertThat(wikipageFound)
            .isPresent()
            .hasValue(wikipageRoxanne)
    }
}
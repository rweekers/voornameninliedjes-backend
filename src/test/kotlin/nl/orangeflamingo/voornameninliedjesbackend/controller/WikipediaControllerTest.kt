package nl.orangeflamingo.voornameninliedjesbackend.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikipediaApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.Optional

class WikipediaControllerTest {

    private val nlApi = mockk<WikipediaApiClient>()
    private val enApi = mockk<WikipediaApiClient>()

    private val wikipediaController = WikipediaController(enApi, nlApi)

    @ParameterizedTest
    @CsvSource(
        "EN, Roxanne",
        "NL, Roxanne",
        "EN, Roxanne_(The_Police_song)",
        "NL, Song-Title",
        "EN, Artist_(disambiguation)",
        "NL, Album-Title-(2024)",
        "EN, ABBA",
        "NL, Queen_(band)"
    )
    fun `valid page names are passed to the correct api`(
        languageCode: String,
        page: String
    ) {
        val language = WikipediaLanguage.valueOf(languageCode)
        val expectedResponse = WikipediaApi("Background for $page")
        val apiToCall = if (language == WikipediaLanguage.EN) enApi else nlApi

        every { apiToCall.getBackground(page) } returns Optional.of(expectedResponse)

        val result = wikipediaController.getLastFmInfoByArtistAndTitle(language, page)

        assertThat(result).isPresent().hasValue(expectedResponse)
        verify(exactly = 1) { apiToCall.getBackground(page) }
    }

    @Test
    fun `when Dutch API returns empty, result is empty`() {
        every { nlApi.getBackground("NonExistent") } returns Optional.empty()

        val result = wikipediaController.getLastFmInfoByArtistAndTitle(
            WikipediaLanguage.NL,
            "NonExistent"
        )

        assertThat(result).isEmpty()
    }

    @Test
    fun `when English API returns empty, result is empty`() {
        every { enApi.getBackground("NonExistent") } returns Optional.empty()

        val result = wikipediaController.getLastFmInfoByArtistAndTitle(
            WikipediaLanguage.EN,
            "NonExistent"
        )

        assertThat(result).isEmpty()
    }

    @Test
    fun `page name with null byte is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            wikipediaController.getLastFmInfoByArtistAndTitle(
                WikipediaLanguage.EN,
                "valid\u0000title"
            )
        }
        verify(exactly = 0) { enApi.getBackground(any()) }
        verify(exactly = 0) { nlApi.getBackground(any()) }
    }

    @Test
    fun `page name with double dots is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            wikipediaController.getLastFmInfoByArtistAndTitle(
                WikipediaLanguage.EN,
                "../etc/passwd"
            )
        }
        verify(exactly = 0) { enApi.getBackground(any()) }
    }

    @Test
    fun `encoded path traversal is detected after decoding`() {
        // %2e%2e decodes to ".."
        assertThrows(IllegalArgumentException::class.java) {
            wikipediaController.getLastFmInfoByArtistAndTitle(
                WikipediaLanguage.NL,
                "%2e%2e"
            )
        }
        verify(exactly = 0) { nlApi.getBackground(any()) }
    }

    @Test
    fun `page name starting with slash is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            wikipediaController.getLastFmInfoByArtistAndTitle(
                WikipediaLanguage.EN,
                "/etc/shadow"
            )
        }
        verify(exactly = 0) { enApi.getBackground(any()) }
    }

    @ParameterizedTest
    @CsvSource(
        "Roxanne The Police",      // space
        "<script>alert('xss')</script>",    // angle brackets
        "\"quote_injection\"",              // quotes
        "C:\\Windows\\System32",            // backslash
        "DROP TABLE; SELECT",               // semicolon
        "page&param=value",                 // ampersand
        "normal|pipe",                      // pipe
        "back`tick",                        // backtick
        "dollar\\$\\sign",                  // dollar sign
        "caret^symbol"                      // caret
    )
    fun `page names with special characters are rejected`(invalidPage: String) {
        assertThrows(IllegalArgumentException::class.java) {
            wikipediaController.getLastFmInfoByArtistAndTitle(
                WikipediaLanguage.EN,
                invalidPage
            )
        }
        verify(exactly = 0) { enApi.getBackground(any()) }
    }

    @Test
    fun `page name exceeding max length is rejected`() {
        val tooLong = "a".repeat(256)
        assertThrows(IllegalArgumentException::class.java) {
            wikipediaController.getLastFmInfoByArtistAndTitle(
                WikipediaLanguage.EN,
                tooLong
            )
        }
    }

    @Test
    fun `page name at exactly max length passes`() {
        val maxLength = "a".repeat(255)
        every { enApi.getBackground(maxLength) } returns Optional.of(WikipediaApi("result"))

        val result = wikipediaController.getLastFmInfoByArtistAndTitle(
            WikipediaLanguage.EN,
            maxLength
        )

        assertThat(result).isPresent()
        verify(exactly = 1) { enApi.getBackground(maxLength) }
    }

    @Test
    fun `empty page name is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            wikipediaController.getLastFmInfoByArtistAndTitle(
                WikipediaLanguage.EN,
                ""
            )
        }
        verify(exactly = 0) { enApi.getBackground(any()) }
    }

    @ParameterizedTest
    @CsvSource(
        "Roxanne, Roxanne",
        "Roxanne_(The_Police_song), Roxanne_(The_Police_song)",
        "Roxanne%5FPolice, Roxanne_Police"
    )
    fun `url encoded characters are properly decoded`(encodedInput: String, expectedOutput: String) {
        every { enApi.getBackground(expectedOutput) } returns Optional.of(WikipediaApi("decoded result"))

        val result = wikipediaController.getLastFmInfoByArtistAndTitle(
            WikipediaLanguage.EN,
            encodedInput
        )

        assertThat(result).isPresent()
        // Verify call with the decoded version
        verify { enApi.getBackground(expectedOutput) }
    }
}
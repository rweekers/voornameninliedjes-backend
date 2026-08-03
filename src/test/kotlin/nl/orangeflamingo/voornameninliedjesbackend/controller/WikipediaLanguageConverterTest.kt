package nl.orangeflamingo.voornameninliedjesbackend.controller

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

class WikipediaLanguageConverterTest {

    private lateinit var converter: WikipediaLanguageConverter

    @BeforeEach
    fun setUp() {
        converter = WikipediaLanguageConverter()
    }

    @ParameterizedTest
    @EnumSource(WikipediaLanguage::class)
    fun `converts valid enum names to language`(expected: WikipediaLanguage) {
        val result = converter.convert(expected.name)

        assertThat(result).isEqualTo(expected)
    }

    @ParameterizedTest
    @ValueSource(strings = ["EN", "en", "En", "eN"])
    fun `converts EN language code case-insensitively`(input: String) {
        val result = converter.convert(input)

        assertThat(result).isEqualTo(WikipediaLanguage.EN)
    }

    @ParameterizedTest
    @ValueSource(strings = ["NL", "nl", "Nl", "nL"])
    fun `converts NL language code case-insensitively`(input: String) {
        val result = converter.convert(input)

        assertThat(result).isEqualTo(WikipediaLanguage.NL)
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "DE",
        "FR",
        "ES",
        "IT",
        "UNKNOWN",
        "",
        "   ",
        " EN"
    ])
    fun `throws exception for unsupported language codes`(invalidCode: String) {
        assertThatThrownBy { converter.convert(invalidCode) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining(invalidCode.uppercase())
    }

    // --- Invalid input: Edge cases ---

    @Test
    fun `throws exception for empty string`() {
        assertThatThrownBy { converter.convert("") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `throws exception for whitespace only`() {
        assertThatThrownBy { converter.convert("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `does not trim whitespace before conversion`() {
        // WikipediaLanguage.valueOf(" EN") would fail
        assertThatThrownBy { converter.convert(" EN") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
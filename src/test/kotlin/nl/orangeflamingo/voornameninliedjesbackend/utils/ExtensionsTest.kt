package nl.orangeflamingo.voornameninliedjesbackend.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExtensionsTest {

    @Test
    fun `test clean string`() {
        val withHash = "Alberta #1"
        val withQuestionmark = "Therapy?"
        val withForwardSlash = "ac/dc"

        assertThat(withHash.clean()).isEqualTo("Alberta 1")
        assertThat(withQuestionmark.clean()).isEqualTo("Therapy")
        assertThat(withForwardSlash.clean()).isEqualTo("acdc")
    }
}
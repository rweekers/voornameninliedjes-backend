package nl.orangeflamingo.voornameninliedjesbackend.utils

import io.github.furstenheim.CopyDown

object Utils {

    private val converter = CopyDown()

    fun html2md(input: String?): String {
        if (input == null) return ""
        return converter.convert(input)
    }

    fun cleanString(input: String): String {
        return input.replace(Regex("['#\\[\\]]"), "")
    }
}
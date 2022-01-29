package nl.orangeflamingo.voornameninliedjesbackend.utils

import io.github.furstenheim.CopyDown
import java.text.Normalizer

private val converter = CopyDown()

fun String.removeDiacritics() =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")

fun String?.html2md(): String {
    if (this == null) return ""
    return converter.convert(this)
}

fun String.cleanForLastFm(): String {
    return this.replace(Regex("[#\\[\\]]"), "")
}

fun String.clean() =
    this.replace(Regex("[?/]"), "")

package nl.orangeflamingo.voornameninliedjesbackend.dto

data class WikipediaSummaryResponse(
    val title: String,
    val extract: String?,
    val description: String? = null,
)
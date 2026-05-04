package nl.orangeflamingo.voornameninliedjesbackend.dto

data class WikipediaApiResponse(
    val batchcomplete: String? = null,
    val query: QueryData
)

data class QueryData(
    val pages: List<PageData>
)

data class PageData(
    val pageid: Int,
    val ns: Int,
    val title: String,
    val extract: String?  // This will contain the full 10-sentence extract
)
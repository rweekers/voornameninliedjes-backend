package nl.orangeflamingo.voornameninliedjesbackend.domain

data class WikipediaSongDto(
    val batchcomplete: String,
    val query: QueryDto
)

data class QueryDto(
    val pages: List<PageDto>
)

data class PageDto(
    val pageid: Long,
    val title: String,
    val extract: String
)
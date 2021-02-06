package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.relational.core.mapping.MappedCollection

class TestSong(
    val id: Long? = null,
    var title: String = "Lucy in the Sky with Diamonds",
    var name: String = "Lucy",
    val artistImage: String? = null,
    var background: String? = "Some background on Lucy in the Sky with Diamonds",
    var youtube: String? = "",
    var spotify: String? = "",
    val wikipediaPage: String? = "",
    var status: SongStatus = SongStatus.SHOW,
    val mongoId: String? = null,
    var sources: List<SongSource> = listOf(),
    val artists: MutableSet<ArtistRef> = mutableSetOf()
) {
    fun toDomain(): Song {
        return Song(
            id = id,
            title = title,
            name = name,
            artistImage = artistImage,
            background = background,
            youtube = youtube,
            spotify = spotify,
            wikipediaPage = wikipediaPage,
            status = status,
            mongoId = mongoId,
            sources = sources,
            artists = artists
        )
    }
}
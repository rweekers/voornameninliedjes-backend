package nl.orangeflamingo.voornameninliedjesbackend.domain

import com.beust.klaxon.Json
import org.springframework.data.jdbc.core.mapping.AggregateReference

class TestSong(
    val id: Long? = null,
    var title: String = "Lucy in the Sky with Diamonds",
    var name: String = "Lucy",
    val artistImage: String? = null,
    val artistImageAttribution: String? = null,
    var background: String? = "Some background on Lucy in the Sky with Diamonds",
    var youtube: String? = "",
    var spotify: String? = "",
    val wikipediaPage: String? = "",
    val wikimediaPhotos: MutableSet<SongPhoto> = mutableSetOf(),
    var status: SongStatus = SongStatus.SHOW,
    val mongoId: String? = null,
    var sources: Set<SongSource> = setOf(),
    @Json(ignored = true)
    val artist: Long? = null
) {
    fun toDomain(): Song {
        return Song(
            id = id,
            title = title,
            name = name,
            artistImage = artistImage,
            artistImageAttribution = artistImageAttribution,
            background = background,
            youtube = youtube,
            spotify = spotify,
            wikipediaPage = wikipediaPage,
            photos = wikimediaPhotos,
            status = status,
            mongoId = mongoId,
            sources = sources,
            artist = AggregateReference.to(artist ?: 0)
        )
    }
}
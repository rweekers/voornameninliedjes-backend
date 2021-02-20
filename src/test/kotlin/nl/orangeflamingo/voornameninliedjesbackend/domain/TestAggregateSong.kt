package nl.orangeflamingo.voornameninliedjesbackend.domain

import com.beust.klaxon.Json
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class TestAggregateSong(
    val id: Long? = 1,
    val title: String = "Michelle",
    val name: String = "Michelle",
    val artistName: String = "The Beatles",
    val artistBackground: String? = "Some background on The Beatles",
    val artistImage: String? = null,
    val background: String? = "Some background on the song Michelle",
    @Json(ignored = true)
    val wikipediaBackground: Mono<String> = Mono.empty(),
    val youtube: String? = "DvYhIotxgOA",
    val spotify: String? = "5By7Pzgl6TMuVJG168VWzS",
    val status: SongStatus = SongStatus.SHOW,
    val mongoId: String? = null,
    val wikimediaPhotos: Set<ArtistWikimediaPhoto> = mutableSetOf(),
    val flickrPhotos: Set<ArtistFlickrPhoto> = mutableSetOf(),
    @Json(ignored = true)
    val flickrPhotoDetail: Flux<PhotoDetail> = Flux.empty(),
    val sources: List<SongSource> = listOf(),
    val logEntries: MutableList<SongLogEntry> = mutableListOf()
) {
    fun toDomain():AggregateSong {
        return AggregateSong(
            id = id,
            title = title,
            name = name,
            artistName = artistName,
            artistBackground = artistBackground,
            artistImage = artistImage,
            background = background,
            wikipediaBackground = wikipediaBackground,
            youtube = youtube,
            spotify = spotify,
            status = status,
            mongoId = mongoId,
            wikimediaPhotos = wikimediaPhotos,
            flickrPhotos = flickrPhotos,
            flickrPhotoDetail = flickrPhotoDetail,
            sources = sources,
            logEntries = logEntries
        )
    }
}
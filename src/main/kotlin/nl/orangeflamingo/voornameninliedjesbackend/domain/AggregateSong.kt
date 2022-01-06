package nl.orangeflamingo.voornameninliedjesbackend.domain

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


data class AggregateSong(
    val id: Long? = null,
    val title: String,
    val name: String,
    val artistName: String,
    val artistBackground: String? = null,
    val artistImage: String? = null,
    val artistImageAttribution: String? = null,
    val background: String? = null,
    val wikipediaPage: String? = null,
    val wikipediaBackground: Mono<String> = Mono.empty(),
    val youtube: String? = null,
    val spotify: String? = null,
    val status: SongStatus,
    val remarks: String? = null,
    val hasDetails: Boolean,
    val mongoId: String? = null,
    val artistWikimediaPhotos: Set<ArtistWikimediaPhoto> = mutableSetOf(),
    val songWikimediaPhotos: Set<SongWikimediaPhoto> = mutableSetOf(),
    val flickrPhotos: Set<ArtistFlickrPhoto> = mutableSetOf(),
    val flickrPhotoDetail: Flux<PhotoDetail> = Flux.empty(),
    val sources: List<SongSource> = listOf(),
    val logEntries: MutableList<SongLogEntry> = mutableListOf()
)
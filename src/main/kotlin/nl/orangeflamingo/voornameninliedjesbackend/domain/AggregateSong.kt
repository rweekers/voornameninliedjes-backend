package nl.orangeflamingo.voornameninliedjesbackend.domain

import reactor.core.publisher.Flux


data class AggregateSong(
    val id: Long,
    val title: String,
    val name: String,
    val artistName: String,
    val artistImage: String? = null,
    val background: String? = null,
    val youtube: String? = null,
    val spotify: String? = null,
    val status: SongStatus,
    val mongoId: String? = null,
    val wikimediaPhotos: Set<ArtistWikimediaPhoto> = mutableSetOf(),
    val flickrPhotos: Set<ArtistFlickrPhoto> = mutableSetOf(),
    val flickrPhotoDetail: Flux<PhotoDetail> = Flux.empty(),
    val sources: MutableList<SongSource> = mutableListOf(),
    val logEntries: MutableList<SongLogEntry> = mutableListOf()
)
package nl.orangeflamingo.voornameninliedjesbackend.domain

import reactor.core.publisher.Flux


data class AggregateSong(
    val id: Long? = null,
    val title: String,
    val name: String,
    val artistName: String,
    val artistBackground: String? = null,
    val artistImage: String? = null,
    val artistImageAttribution: String? = null,
    val artistMbid: String? = null,
    val artistLastFmUrl: String? = null,
    val background: String? = null,
    val wikipediaPage: String? = null,
    val youtube: String? = null,
    val spotify: String? = null,
    val wikipediaContentNl: String? = null,
    val wikipediaContentEn: String? = null,
    val wikipediaSummaryEn: String? = null,
    val mbid: String? = null,
    val lastFmUrl: String? = null,
    val albumName: String? = null,
    val albumMbid: String? = null,
    val albumLastFmUrl: String? = null,
    val status: SongStatus,
    val remarks: String? = null,
    val hasDetails: Boolean,
    val mongoId: String? = null,
    val artistWikimediaPhotos: Set<ArtistWikimediaPhoto> = mutableSetOf(),
    val songWikimediaPhotos: Set<SongWikimediaPhoto> = mutableSetOf(),
    val flickrPhotos: Set<ArtistFlickrPhoto> = mutableSetOf(),
    val flickrPhotoDetail: Flux<PhotoDetail> = Flux.empty(),
    val sources: List<SongSource> = listOf(),
    val tags: Set<SongLastFmTag> = setOf(),
    val logEntries: MutableList<SongLogEntry> = mutableListOf()
)
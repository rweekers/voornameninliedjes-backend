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
    val localImage: String? = null,
    val blurredImage: String? = null,
    val artistImageWidth: Int? = null,
    val artistImageHeight: Int? = null,
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
    val artistPhotos: Set<ArtistPhoto> = mutableSetOf(),
    val songPhotos: Set<SongPhoto> = mutableSetOf(),
    val flickrPhotoDetail: Flux<PhotoDetail> = Flux.empty(),
    val sources: Set<SongSource> = setOf(),
    val tags: List<SongLastFmTag> = listOf(),
    val logEntries: MutableSet<SongLogEntry> = mutableSetOf()
)
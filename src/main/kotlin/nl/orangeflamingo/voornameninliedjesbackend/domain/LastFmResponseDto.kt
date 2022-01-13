package nl.orangeflamingo.voornameninliedjesbackend.domain

data class LastFmResponseDto(
    val error: String? = null,
    val message: String? = null,
    val track: LastFmTrackDto?
)

data class LastFmTrackDto(
    val name: String,
    val mbid: String?,
    val url: String,
    val artist: LastFmArtistDto,
    val album: LastFmAlbumDto?,
    val toptags: LastFmTopTagsDto,
    val wiki: LastFmWikiDto?
)

data class LastFmArtistDto(
    val name: String,
    val mbid: String?,
    val url: String
)

data class LastFmAlbumDto(
    val artist: String,
    val title: String,
    val mbid: String,
    val url: String
)

data class LastFmTopTagsDto(
    val tag: List<LastFmTagDto>
)

data class LastFmTagDto(
    val name: String,
    val url: String
)

data class LastFmWikiDto(
    val summary: String,
    val content: String
)
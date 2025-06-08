package nl.orangeflamingo.voornameninliedjesbackend.domain

sealed interface LastFmResponse

data class LastFmError(
        val code: String,
        val message: String?
): LastFmResponse

data class LastFmTrack(
        val name: String,
        val url: String,
        val mbid: String?,
        val album: LastFmAlbum?,
        val artist: LastFmArtist,
        val tags: List<LastFmTag>,
        val wiki: LastFmWiki?
): LastFmResponse

data class LastFmAlbum(
        val name: String,
        val mbid: String?,
        val url: String
)

data class LastFmArtist(
        val name: String,
        val mbid: String?,
        val url: String
)

data class LastFmTag(
        val name: String,
        val url: String
)

data class LastFmWiki(
        val summary: String,
        val content: String
)
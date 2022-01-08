package nl.orangeflamingo.voornameninliedjesbackend.domain


data class LastFmTrack(
        val name: String,
        val url: String,
        val album: LastFmAlbum?,
        val artist: LastFmArtist,
        val tags: List<LastFmTag>,
        val wiki: LastFmWiki?
)

data class LastFmAlbum(
        val name: String,
        val url: String
)

data class LastFmArtist(
        val name: String,
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
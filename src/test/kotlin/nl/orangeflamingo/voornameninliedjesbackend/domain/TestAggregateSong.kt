package nl.orangeflamingo.voornameninliedjesbackend.domain

data class TestAggregateSong(
    val id: Long? = 1,
    val title: String = "Michelle",
    val name: String = "Michelle",
    val artistName: String = "The Beatles",
    val artistBackground: String? = "Some background on The Beatles",
    val artistImage: String? = null,
    val background: String? = "Some background on the song Michelle",
    val youtube: String? = "DvYhIotxgOA",
    val spotify: String? = "5By7Pzgl6TMuVJG168VWzS",
    val status: SongStatus = SongStatus.SHOW,
    val hasDetails: Boolean = false,
    val mongoId: String? = null,
    val wikimediaPhotos: Set<ArtistPhoto> = mutableSetOf(),
    val sources: Set<SongSource> = setOf(),
    val logEntries: MutableSet<SongLogEntry> = mutableSetOf()
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
            youtube = youtube,
            spotify = spotify,
            status = status,
            hasDetails = hasDetails,
            mongoId = mongoId,
            artistPhotos = wikimediaPhotos,
            sources = sources,
            logEntries = logEntries
        )
    }
}
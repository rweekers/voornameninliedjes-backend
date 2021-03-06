package nl.orangeflamingo.voornameninliedjesbackend.dto

data class TestSongDto(
    val id: String? = null,
    val artist: String = "The Beatles",
    val title: String = "Lucy in the Sky with Diamonds",
    val name: String = "Lucy",
    val artistImage: String? = null,
    val background: String? = "Some background on Lucy in the Sky with Diamonds",
    val wikipediaPage: String? = "wikiPage",
    val youtube: String? = "",
    val spotify: String? = "",
    val wikimediaPhotos: Set<WikimediaPhotoDto> = emptySet(),
    val flickrPhotos: Set<PhotoDto> = emptySet(),
    val sources: Set<SourceDto> = emptySet(),
    val status: String = "SHOW"
) {
    fun toDomain(): SongDto {
        return SongDto(
            id = id,
            artist = artist,
            title = title,
            name = name,
            artistImage = artistImage,
            background = background,
            wikipediaPage = wikipediaPage,
            youtube = youtube,
            spotify = spotify,
            wikimediaPhotos = wikimediaPhotos,
            flickrPhotos = flickrPhotos,
            sources = sources,
            status = status
        )
    }
}
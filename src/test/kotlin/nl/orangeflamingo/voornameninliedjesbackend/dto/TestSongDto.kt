package nl.orangeflamingo.voornameninliedjesbackend.dto

data class TestSongDto(
        val id: String?,
        val artist: String,
        val title: String,
        val name: String,
        val artistImage: String?,
        val background: String?,
        val youtube: String?,
        val spotify: String?,
        val wikimediaPhotos: Set<WikimediaPhotoDto>?,
        val flickrPhotos: Set<PhotoDto>?,
        val sources: Set<SourceDto>?,
        val status: String?
)
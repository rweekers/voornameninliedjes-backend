package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SongEnrichmentService(
    @Autowired val songRepository: SongRepository,
    @Autowired val artistRepository: ArtistRepository,
    @Autowired val flickrApiClient: FlickrApiClient
) {

    private val log = LoggerFactory.getLogger(SongEnrichmentService::class.java)

    fun enrichSongs(updateAll: Boolean = false) {
        log.info("Starting enrichment with update all: $updateAll")

        val songsToUpdate =
            if (updateAll) songRepository.findAllByStatusOrderedByName(SongStatus.SHOW.code)
            else songRepository.findAllByStatusAndArtistImageIsNullOrArtistImageAttributionIsNull(
                SongStatus.SHOW.code
            )
        songsToUpdate.forEach { updateArtistImageForSong(it) }
    }

    private fun updateArtistImageForSong(song: Song) {
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()
        val urlToAttribution =
            if (song.wikimediaPhotos.isNotEmpty()) song.wikimediaPhotos.map { it.url to it.attribution }
                .firstOrNull() else artist.wikimediaPhotos.map { it.url to it.attribution }
                .firstOrNull()
        if (urlToAttribution != null) {
            val (url, attribution) = urlToAttribution
            updateArtistImage(url, attribution, song)
        } else {
            val photo = flickrApiClient.getPhoto(artist.flickrPhotos.first().flickrId)
            photo.subscribe { p ->
                flickrApiClient.getOwnerInformation(p.ownerId).subscribe { o ->
                    val attribution = "Photo by ${o.username} to be found at ${p.url}"
                    updateArtistImage(p.url, attribution, song)
                }
            }
        }
    }

    private fun updateArtistImage(url: String, attribution: String, song: Song) {
        if (url != song.artistImage || attribution != song.artistImageAttribution) {
            val updatedSong = song.copy(artistImage = url, artistImageAttribution = attribution)
            songRepository.save(updatedSong)
        }
    }

}
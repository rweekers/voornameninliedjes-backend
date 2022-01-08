package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LastFmEnrichmentService(
    @Autowired val songRepository: SongRepository,
    @Autowired val artistRepository: ArtistRepository,
    @Autowired val lastFmApiClient: LastFmApiClient
) {

    private val log = LoggerFactory.getLogger(ImagesEnrichmentService::class.java)

    fun enrichLastFmInfoForSongs() {
        log.info("Starting last fm enrichment")

        val songsToUpdate = songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)
        songsToUpdate.forEach { updateArtistImageForSong(it) }
    }

    private fun updateArtistImageForSong(song: Song) {
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artists.first { it.originalArtist }} for song with title ${song.title} not found") }

        try {
            log.info("Updating ${song.title} from ${artist.name}")

            val lastFmTrack = lastFmApiClient.getTrack(artist.name, song.title)

            lastFmTrack.subscribe {
                it.tags.forEach { tag ->
                    song.lastFmTags.add(
                        SongLastFmTag(
                            name = tag.name,
                            url = tag.url
                        )
                    )
                }
                songRepository.save(song)
            }
        } catch (e: Exception) {
            log.error("Could not update last fm information for ${artist.name} - ${song.title} due to error", e)
        }
    }
}
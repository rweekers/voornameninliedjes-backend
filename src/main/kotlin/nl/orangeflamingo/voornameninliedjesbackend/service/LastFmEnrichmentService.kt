package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.utils.Utils.html2md
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

    fun enrichLastFmInfoForSongs(updateAll: Boolean = false) {
        log.info("Starting last fm enrichment")

        val songsToUpdate =
            if (updateAll) songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)
            else songRepository.findAllByStatusAndMbidIsNullOrderedByNameAndTitle(
                SongStatus.SHOW.code
            )

        songsToUpdate.forEach { updateLastFmInfoForSong(it) }
    }

    private fun updateLastFmInfoForSong(song: Song) {
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artists.first { it.originalArtist }} for song with title ${song.title} not found") }

        try {
            log.info("[last fm] Updating ${song.title} from ${artist.name}")

            val lastFmTrack = lastFmApiClient.getTrack(artist.name, song.title)

            lastFmTrack.subscribe {
                song.mbid = it.mbid
                song.lastFmUrl = it.url
                song.wikiSummaryEn = html2md(it.wiki?.summary)
                song.wikiContentEn = html2md(it.wiki?.content)
                artist.mbid = it.artist.mbid
                artist.lastFmUrl = it.artist.url
                it.tags.forEach { tag ->
                    song.lastFmTags.add(
                        SongLastFmTag(
                            name = tag.name,
                            url = tag.url
                        )
                    )
                }
                songRepository.save(song)
                artistRepository.save(artist)
            }
        } catch (e: Exception) {
            log.error("Could not update last fm information for ${artist.name} - ${song.title} due to error", e)
        }
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.LastFmApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmError
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrack
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import nl.orangeflamingo.voornameninliedjesbackend.utils.clean
import nl.orangeflamingo.voornameninliedjesbackend.utils.html2md
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class LastFmEnrichmentService @Autowired constructor(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val lastFmApiClient: LastFmApiClient
) {

    private val log = LoggerFactory.getLogger(ImagesEnrichmentService::class.java)

    fun enrichLastFmInfoForSongs(updateAll: Boolean = false) {
        log.info("Starting last fm enrichment with update all: $updateAll")

        val songsToUpdate =
            if (updateAll) songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)
            else songRepository.findAllByStatusAndLastFmUrlIsNullOrderedByNameAndTitle(
                SongStatus.SHOW.code
            )

        songsToUpdate.forEach { updateLastFmInfoForSong(it) }
    }

    private fun updateLastFmInfoForSong(song: Song) {
        val artist = artistRepository.findById(song.artist.id ?: throw IllegalStateException())
            .orElseThrow { ArtistNotFoundException("Artist with id ${song.artist.id} for song with title ${song.title} not found") }

        try {
            log.info("[last fm] Updating ${song.title} from ${artist.name}")

            val lastFmTrack =
                lastFmApiClient.getTrack(
                    artist.name.replace("'", ""),
                    song.title.clean()
                )

            lastFmTrack.subscribe({
                when (it) {
                    is LastFmTrack -> {
                        song.mbid = it.mbid
                        song.lastFmUrl = it.url
                        song.wikiSummaryEn = it.wiki?.summary.html2md()
                        song.wikiContentEn = it.wiki?.content.html2md()
                        song.albumName = it.album?.name
                        song.albumMbid = it.album?.mbid
                        song.albumLastFmUrl = it.album?.url
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
                    is LastFmError -> log.warn("Error calling last fm api for ${artist.name} - ${song.title}. Gotten code ${it.code} and message ${it.message}")
                }
            },
                {
                    log.warn("Gotten eror with message ${it.message}")
                })
        } catch (e: Exception) {
            log.error("Could not update last fm information for ${artist.name} - ${song.title} due to error", e)
        }
    }
}
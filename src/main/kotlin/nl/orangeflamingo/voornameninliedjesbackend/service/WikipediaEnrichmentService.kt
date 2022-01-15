package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class WikipediaEnrichmentService @Autowired constructor(
    private val songRepository: SongRepository,
    private val wikipediaApiClient: WikipediaApiClient
) {

    private val log = LoggerFactory.getLogger(ImagesEnrichmentService::class.java)

    fun enrichWikipediaForSongs(updateAll: Boolean = false) {
        log.info("Starting wikipedia enrichment with update all: $updateAll")

        val songsToUpdate =
            if (updateAll) songRepository.findAllByStatusOrderedByNameAndTitle(SongStatus.SHOW.code)
            else songRepository.findAllByStatusAndWikipediaPageIsNotNullAndWikiContentNlIsNullOrderedByNameAndTitle(
                SongStatus.SHOW.code
            )

        songsToUpdate.forEach { updateWikipediaForSong(it) }
    }

    private fun updateWikipediaForSong(song: Song) {
        try {
            log.info("[wikipedia] Updating ${song.title}")

            val wikipediaInformation =
                if (song.wikipediaPage != null) wikipediaApiClient.getBackground(song.wikipediaPage!!) else null

            wikipediaInformation?.subscribe {
                song.wikiContentNl = it.background
                songRepository.save(song)
            }
        } catch (e: Exception) {
            log.error(
                "Could not update last wikipedia information for wiki page ${song.wikipediaPage} for title ${song.title} due to error",
                e
            )
        }
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ArtistService {

    private val log = LoggerFactory.getLogger(ArtistService::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var mongoSongService: MongoSongService

    fun countArtists(): Long {
        return artistRepository.count()
    }

    fun migrateArtists() {
        mongoSongService.findAllArtistNames()
            .forEach {
                artistRepository.save(
                    Artist(
                        name = it,
                        background = null,
                        wikimediaPhotos = mutableSetOf(),
                        flickrPhotos = mutableSetOf(),
                        logEntries = mutableListOf(
                            ArtistLogEntry(
                                date = Instant.now(),
                                username = "Mongo2PostgresMigration"
                            )
                        )
                    )
                )
            }
    }
}
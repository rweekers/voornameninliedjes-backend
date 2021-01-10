package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ArtistService {

    private val log = LoggerFactory.getLogger(ArtistService::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    fun countArtists(): Long {
        log.info("Count artists")
        return artistRepository.count()
    }

}
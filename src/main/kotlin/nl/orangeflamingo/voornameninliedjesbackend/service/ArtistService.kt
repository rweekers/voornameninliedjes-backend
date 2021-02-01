package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ArtistService {

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    fun findByName(name: String): List<Artist> {
        return artistRepository.findByNameIgnoreCase(name)
    }

}
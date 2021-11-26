package nl.orangeflamingo.voornameninliedjesbackend.cucumber.steps

import io.cucumber.java.Before
import io.cucumber.spring.CucumberContextConfiguration
import nl.orangeflamingo.voornameninliedjesbackend.IntegrationTestConfiguration
import nl.orangeflamingo.voornameninliedjesbackend.SongApplication
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@CucumberContextConfiguration
@SpringBootTest(classes = [IntegrationTestConfiguration::class])
@ContextConfiguration(classes = [SongApplication::class], loader = SpringBootContextLoader::class)
@ActiveProfiles("integration-test")
class CucumberHooksSteps {

    private val log = LoggerFactory.getLogger(CucumberHooksSteps::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var songRepository: SongRepository

    @Before
    fun before() {
        log.info("Running before steps")
        clean()
    }

    private fun clean() {
        log.info("Cleaning database")
        songRepository.deleteAll()
        artistRepository.deleteAll()
    }
}
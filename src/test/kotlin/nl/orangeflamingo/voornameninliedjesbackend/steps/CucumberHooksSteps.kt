package nl.orangeflamingo.voornameninliedjesbackend.steps

import io.cucumber.java8.En
import io.cucumber.java8.Scenario
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
import org.springframework.test.context.TestExecutionListeners

@CucumberContextConfiguration
@SpringBootTest(classes = [IntegrationTestConfiguration::class])
@ContextConfiguration(classes = [SongApplication::class], loader = SpringBootContextLoader::class)
@ActiveProfiles("integration-test")
@TestExecutionListeners
class CucumberHooksSteps : En {

    private val log = LoggerFactory.getLogger(CucumberHooksSteps::class.java)

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Autowired
    private lateinit var songRepository: SongRepository

    init {
        Before { _: Scenario ->
            log.info("Running before steps")
            clean()
        }
    }

    private fun clean() {
        log.info("Cleaning database")
        songRepository.deleteAll()
        artistRepository.deleteAll()
    }
}
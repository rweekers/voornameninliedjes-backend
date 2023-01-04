package nl.orangeflamingo.voornameninliedjesbackend.cucumber.steps

import io.cucumber.java.Before
import io.cucumber.spring.CucumberContextConfiguration
import nl.orangeflamingo.voornameninliedjesbackend.AbstractIntegrationTest
import nl.orangeflamingo.voornameninliedjesbackend.IntegrationTestConfiguration
import nl.orangeflamingo.voornameninliedjesbackend.SongApplication
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@CucumberContextConfiguration
@SpringBootTest(classes = [IntegrationTestConfiguration::class])
@ContextConfiguration(classes = [SongApplication::class], loader = SpringBootContextLoader::class)
class CucumberHooksSteps: AbstractIntegrationTest() {

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
//
//    companion object {
//
//        @JvmStatic
//        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:13.1"))
//            .withExposedPorts(5432)
//            .withUsername("vil_app")
//            .withPassword("secret")
//            .withDatabaseName("voornameninliedjes")
//
//        @BeforeAll
//        @JvmStatic
//        fun before_or_after_all() {
//            postgresContainer.start()
//        }
//
//        @JvmStatic
//        @DynamicPropertySource
//        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
//
//            registry.add("voornameninliedjes.datasource.application.host", postgresContainer::getHost)
//            registry.add("voornameninliedjes.datasource.application.port", { postgresContainer.getMappedPort(5432) })
//
//            registry.add("voornameninliedjes.datasource.migration.host", postgresContainer::getHost)
//            registry.add("voornameninliedjes.datasource.migration.port", { postgresContainer.getMappedPort(5432) })
//
//            registry.add("voornameninliedjes.datasource.migration.username", postgresContainer::getUsername)
//            registry.add("voornameninliedjes.datasource.migration.password", postgresContainer::getPassword)
//        }
//    }
}
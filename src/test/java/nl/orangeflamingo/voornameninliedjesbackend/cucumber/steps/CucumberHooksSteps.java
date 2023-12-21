package nl.orangeflamingo.voornameninliedjesbackend.cucumber.steps;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import nl.orangeflamingo.voornameninliedjesbackend.IntegrationTestConfiguration;
import nl.orangeflamingo.voornameninliedjesbackend.SongApplication;
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository;
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@CucumberContextConfiguration
@SpringBootTest(classes = {IntegrationTestConfiguration.class})
@ContextConfiguration(classes = {SongApplication.class}, loader = SpringBootContextLoader.class)
@ActiveProfiles("integration-test")
public class CucumberHooksSteps {

    private static final Logger log = LoggerFactory.getLogger(CucumberHooksSteps.class);

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
            "postgres:15.4-bookworm"
    ).withExposedPorts(5432)
            .withUsername("vil_app")
            .withPassword("secret")
            .withDatabaseName("voornameninliedjes");

    @BeforeAll
    public static void beforeAll() {
        postgresContainer.start();
    }

    @DynamicPropertySource
    public static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("voornameninliedjes.datasource.application.jdbc-url", () ->
                String.format("jdbc:postgresql://%s:%d/%s",
                        postgresContainer.getHost(),
                        postgresContainer.getMappedPort(5432),
                        postgresContainer.getDatabaseName()
                )
        );

        registry.add("voornameninliedjes.datasource.migration.jdbc-url", () ->
                String.format("jdbc:postgresql://%s:%d/%s",
                        postgresContainer.getHost(),
                        postgresContainer.getMappedPort(5432),
                        postgresContainer.getDatabaseName()
                )
        );

        registry.add("voornameninliedjes.datasource.migration.username", postgresContainer::getUsername);
        registry.add("voornameninliedjes.datasource.migration.password", postgresContainer::getPassword);
    }

    @Before
    public void before() {
        log.info("Running before steps");
        clean();
    }

    private void clean() {
        log.info("Cleaning database");
        songRepository.deleteAll();
        artistRepository.deleteAll();
    }
}
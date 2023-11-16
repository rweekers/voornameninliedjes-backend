package nl.orangeflamingo.voornameninliedjesbackend

import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
abstract class AbstractIntegrationTest {
    companion object {

        @JvmStatic
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:15.4-bookworm"))
            .withExposedPorts(5432)
            .withUsername("vil_app")
            .withPassword("secret")
            .withDatabaseName("voornameninliedjes")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgresContainer.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {

            registry.add("voornameninliedjes.datasource.application.jdbc-url") {
                "jdbc:postgresql://${postgresContainer.host}:${postgresContainer.getMappedPort(5432)}/${postgresContainer.databaseName}"
            }

            registry.add("voornameninliedjes.datasource.migration.jdbc-url") {
                "jdbc:postgresql://${postgresContainer.host}:${postgresContainer.getMappedPort(5432)}/${postgresContainer.databaseName}"
            }

            registry.add("voornameninliedjes.datasource.migration.username", postgresContainer::getUsername)
            registry.add("voornameninliedjes.datasource.migration.password", postgresContainer::getPassword)
        }
    }
}
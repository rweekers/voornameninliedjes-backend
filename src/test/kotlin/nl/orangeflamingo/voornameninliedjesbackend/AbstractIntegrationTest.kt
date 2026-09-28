package nl.orangeflamingo.voornameninliedjesbackend

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("integration-test")
abstract class AbstractIntegrationTest {

    private val admin: String = "admin"
    private val adminPassword: String = "admin"
    private val owner: String = "owner"
    private val ownerPassword: String = "owner"

    companion object {

        @JvmStatic
        val postgresContainer: PostgreSQLContainer = PostgreSQLContainer(DockerImageName.parse("postgres:18.4-bookworm"))
            .withExposedPorts(5432)
            .withUsername("vil_app")
            .withPassword("secret")
            .withDatabaseName("voornameninliedjes")

        @JvmStatic
        val keycloakContainer: KeycloakContainer = KeycloakContainer("quay.io/keycloak/keycloak:26.7.4")
            .withRealmImportFile("/realm.json")
            .withAdminUsername("admin")
            .withAdminPassword("admin")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgresContainer.start()
            keycloakContainer.start()
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

            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") {
                keycloakContainer.getIssuerUrl("voornameninliedjes")
            }
        }
    }

    protected fun getAdminToken(): String =
        keycloakContainer.getAccessToken(
            "voornameninliedjes",
            "voornameninliedjes-test",
            admin,
            adminPassword
        )

    protected fun getOwnerToken(): String =
        keycloakContainer.getAccessToken(
            "voornameninliedjes",
            "voornameninliedjes-test",
            owner,
            ownerPassword
        )
}
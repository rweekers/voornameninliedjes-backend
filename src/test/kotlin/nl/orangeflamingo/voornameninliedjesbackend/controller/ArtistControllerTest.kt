package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@AutoConfigureWebTestClient
class ArtistControllerTest(
    @Autowired val client: WebTestClient,
    @Autowired val songRepository: SongRepository,
    @Autowired val artistRepository: ArtistRepository,
) {
    private lateinit var artistMap: Map<String, Long>

    @BeforeEach
    fun createUser() {
        songRepository.deleteAll()
        artistRepository.deleteAll()
        val artist = Artist(
            name = "The Beatles",
            flickrPhotos = mutableSetOf(ArtistFlickrPhoto("1"), ArtistFlickrPhoto("2")),
            wikimediaPhotos = mutableSetOf(
                ArtistWikimediaPhoto(
                    url = "https://upload.wikimedia.org/wikipedia/commons/6/61/The_Beatles_arrive_at_JFK_Airport.jpg",
                    attribution = "United Press International, Public domain, via Wikimedia Commons"
                )
            )
        )
        artistMap = artistRepository.saveAll(
            listOf(
                artist,
                artist.copy(
                    name = "The Rolling Stones",
                    flickrPhotos = mutableSetOf(),
                    wikimediaPhotos = mutableSetOf()
                )
            )
        ).map { it.name to it.id!! }.toMap()
    }

    @Test
    fun getAllArtistsTest() {
        client.get()
            .uri("/api/artists")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<ArtistDto>().hasSize(2)
    }

    @Test
    fun getArtistsByNameTest() {
        client.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/artists")
                    .queryParam("name", "The Rolling Stones")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBodyList<ArtistDto>().hasSize(1)
    }

    @Test
    fun getArtistByIdTest() {
        client.get()
            .uri("/api/artists/${artistMap["The Beatles"]}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isNotEmpty
            .jsonPath("$.name").isEqualTo("The Beatles")
            .jsonPath("$.flickrPhotos").isNotEmpty
            .jsonPath("$.flickrPhotos[0].flickrId").isEqualTo("1")
    }

    companion object {

        @Container
        @JvmStatic
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:13.1"))
            .withExposedPorts(5432)
            .waitingFor(HttpWaitStrategy().forPort(5432))
            .withUsername("vil_app")
            .withPassword("secret")

    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            postgresContainer.start()

            TestPropertyValues.of(
                "voornameninliedjes.datasource.application.host=${postgresContainer.host}",
                "voornameninliedjes.datasource.application.port=${postgresContainer.firstMappedPort}",
                "voornameninliedjes.datasource.migration.host=${postgresContainer.host}",
                "voornameninliedjes.datasource.migration.port=${postgresContainer.firstMappedPort}",
                "voornameninliedjes.datasource.migration.username=${postgresContainer.username}",
                "voornameninliedjes.datasource.migration.password=${postgresContainer.password}"
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}


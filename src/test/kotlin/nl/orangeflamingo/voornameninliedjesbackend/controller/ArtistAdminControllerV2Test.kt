package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import nl.orangeflamingo.voornameninliedjesbackend.command.ArtistPhotoCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.command.UpdateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.config.CorsConfig
import nl.orangeflamingo.voornameninliedjesbackend.config.SecurityConfig
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import java.net.URI
import java.time.Instant
import java.util.UUID

@WebMvcTest(ArtistAdminControllerV2::class)
@EnableConfigurationProperties(CorsConfig::class)
@Import(SecurityConfig::class)
@TestPropertySource(
    properties = [
        "voornameninliedjes.cors.domains.allowed.api[0]=http://localhost",
        "voornameninliedjes.cors.domains.allowed.admin[0]=http://localhost"
    ]
)
class ArtistAdminControllerV2Test {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockkBean private lateinit var artistService: ArtistService
    @Suppress("unused") @MockkBean private lateinit var cacheManager: CacheManager
    @Suppress("unused") @MockkBean private lateinit var jwtDecoder: JwtDecoder
    @Autowired
    private lateinit var handlerAdapter: RequestMappingHandlerAdapter

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `should get all artists ordered by name`() {
        val artists = listOf(
            Artist(
                id = 1L,
                name = "The Police"
            ),
            Artist(
                id = 2L,
                name = "ABBA"
            )
        )

        every { artistService.findAllOrderedByName() } returns artists

        mockMvc.perform(
            get("/admin/artists")
                .with(user("testuser").roles("ADMIN"))
                .accept("application/vnd.voornameninliedjes.artists.v2+json")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("The Police"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("ABBA"))

        verify(exactly = 1) {
            artistService.findAllOrderedByName()
        }

        verify(exactly = 0) {
            artistService.search(any())
        }
    }

    @Test
    fun `should search artists`() {
        val artists = listOf(
            Artist(
                id = 42L,
                name = "The Police"
            )
        )

        every { artistService.search("police") } returns artists

        mockMvc.perform(
            get("/admin/artists")
                .with(user("testuser").roles("ADMIN"))
                .param("search", "police")
                .accept("application/vnd.voornameninliedjes.artists.v2+json")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(42))
            .andExpect(jsonPath("$[0].name").value("The Police"))

        verify(exactly = 1) {
            artistService.search("police")
        }

        verify(exactly = 0) {
            artistService.findAllOrderedByName()
        }
    }

    @Test
    fun `should get artist by id`() {
        val artist = Artist(
            id = 42L,
            name = "The Police"
        )

        every { artistService.findById(42L) } returns artist

        mockMvc.perform(
            get("/admin/artists/42")
                .with(user("testuser").roles("ADMIN"))
                .accept("application/vnd.voornameninliedjes.artists.v2+json")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.name").value("The Police"))

        verify(exactly = 1) {
            artistService.findById(42L)
        }
    }

    @Test
    fun `should create artist`() {
        val artist = Artist(
            id = 42L,
            name = "The Police",
            logEntries = mutableSetOf(
                ArtistLogEntry(
                    date = Instant.ofEpochSecond(1000),
                    username = "testuser"
                ),
                ArtistLogEntry(
                    date = Instant.ofEpochSecond(1000),
                    username = "testuser"
                )
            )
        )

        every { artistService.create(any(CreateArtistCommand::class)) } returns artist

        mockMvc.perform(
            post("/admin/artists")
                .with(user("testuser").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.voornameninliedjes.artists.v2+json")
                .content(
                    """
                {
                  "name": "The Police",
                  "background": "English rock band",
                  "mbid": "2db3f6e2-0e5d-4f8e-9e9f-4e9e0e6e9e9e",
                  "lastFmUrl": "https://www.last.fm/music/The+Police",
                  "photos": [
                    {
                      "imageUrl": "https://example.com/police.jpg",
                      "imageAttribution": "Photo by Example"
                    }
                  ]
                }
                """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.name").value("The Police"))

        verify {
            artistService.create(
                CreateArtistCommand(
                    name = "The Police",
                    background = "English rock band",
                    mbid = UUID.fromString("2db3f6e2-0e5d-4f8e-9e9f-4e9e0e6e9e9e"),
                    lastFmUrl = URI("https://www.last.fm/music/The+Police"),
                    photos = setOf(
                        ArtistPhotoCommand(
                            url = URI("https://example.com/police.jpg"),
                            attribution = "Photo by Example"
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `should update artist`() {
        val artist = Artist(
            id = 42L,
            name = "The Police"
        )

        every { artistService.update(any(UpdateArtistCommand::class)) } returns artist

        mockMvc.perform(
            put("/admin/artists/42")
                .with(user("testuser").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.voornameninliedjes.artists.v2+json")
                .content(
                    """
                {
                  "name": "The Police",
                  "background": "English rock band",
                  "mbid": "2db3f6e2-0e5d-4f8e-9e9f-4e9e0e6e9e9e",
                  "lastFmUrl": "https://www.last.fm/music/The+Police",
                  "photos": [
                    {
                      "imageUrl": "https://example.com/police.jpg",
                      "imageAttribution": "Photo by Example"
                    }
                  ]
                }
                """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.name").value("The Police"))

        verify {
            artistService.update(
                UpdateArtistCommand(
                    id = 42L,
                    name = "The Police",
                    background = "English rock band",
                    mbid = UUID.fromString("2db3f6e2-0e5d-4f8e-9e9f-4e9e0e6e9e9e"),
                    lastFmUrl = URI("https://www.last.fm/music/The+Police"),
                    photos = setOf(
                        ArtistPhotoCommand(
                            url = URI("https://example.com/police.jpg"),
                            attribution = "Photo by Example"
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `should delete artist`() {
        every { artistService.delete(42L) } just Runs

        mockMvc.perform(
            delete("/admin/artists/42")
                .with(user("testuser").roles("OWNER"))
        )
            .andExpect(status().isNoContent)

        verify(exactly = 1) {
            artistService.delete(42L)
        }
    }

    @Test
    fun `should return 401 when not authenticated`() {
        mockMvc.perform(
            post("/admin/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.voornameninliedjes.artists.v2+json")
                .content("""{"name":"The Police"}""")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should return 403 when user is not admin`() {
        mockMvc.perform(
            post("/admin/artists")
                .with(user("testuser").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.voornameninliedjes.artists.v2+json")
                .content("""{"name":"The Police"}""")
        )
            .andExpect(status().isForbidden)
    }
}
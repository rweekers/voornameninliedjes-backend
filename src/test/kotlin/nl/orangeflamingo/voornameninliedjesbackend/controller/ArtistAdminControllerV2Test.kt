package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.config.CorsConfig
import nl.orangeflamingo.voornameninliedjesbackend.config.MyBasicAuthPoint
import nl.orangeflamingo.voornameninliedjesbackend.config.SecurityConfig
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

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
    @Suppress("unused") @MockkBean private lateinit var userRepository: UserRepository
    @Suppress("unused") @MockkBean private lateinit var authenticationEntryPoint: MyBasicAuthPoint
    @Autowired
    private lateinit var handlerAdapter: RequestMappingHandlerAdapter

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `should create artist`() {
        val artist = Artist(
            id = 42L,
            name = "The Police"
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
                  "imageUrl": "https://example.com/police.jpg",
                  "imageAttribution": "Photo by Example"
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
                    imageUrl = "https://example.com/police.jpg",
                    imageAttribution = "Photo by Example"
                )
            )
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
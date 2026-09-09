package nl.orangeflamingo.voornameninliedjesbackend.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.config.CorsConfig
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ArtistAdminControllerV2::class)
@EnableConfigurationProperties(CorsConfig::class)
@TestPropertySource(
    properties = [
        "voornameninliedjes.cors.domains.allowed.api[0]=http://localhost",
        "voornameninliedjes.cors.domains.allowed.admin[0]=http://localhost"
    ]
)
class ArtistAdminControllerV2Test {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockkBean private lateinit var artistService: ArtistService
    @MockkBean private lateinit var cacheManager: CacheManager

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should create artist`() {
        val artist = Artist(
            id = 42L,
            name = "The Police"
        )

        every { artistService.create(any(CreateArtistCommand::class)) } returns artist

        val result = mockMvc.perform(
            post("/admin/artists")
            // ...
        ).andReturn()

        println(result.resolvedException)
        result.resolvedException?.printStackTrace()

        mockMvc.perform(
            post("/admin/artists")
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

        verify(artistService).create(
            CreateArtistCommand(
                name = "The Police",
                background = "English rock band",
                imageUrl = "https://example.com/police.jpg",
                imageAttribution = "Photo by Example"
            )
        )
    }

}
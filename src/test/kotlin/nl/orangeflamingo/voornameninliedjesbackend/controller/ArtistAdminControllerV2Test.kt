package nl.orangeflamingo.voornameninliedjesbackend.controller

import io.mockk.every
import nl.orangeflamingo.voornameninliedjesbackend.command.CreateArtistCommand
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.service.ArtistService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ArtistAdminControllerV2::class)
@Disabled
class ArtistAdminControllerV2Test {

    @Autowired private lateinit var mockMvc: MockMvc
    @MockitoBean private lateinit var artistService: ArtistService
    @MockitoBean private lateinit var cacheManager: CacheManager

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should create artist`() {
        val artist = Artist(
            id = 42L,
            name = "The Police"
        )

        every { artistService.create(any(CreateArtistCommand::class)) } returns artist

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
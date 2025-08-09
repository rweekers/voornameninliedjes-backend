package nl.orangeflamingo.voornameninliedjesbackend.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenApiServers(): OpenAPI {
        return OpenAPI()
            .servers(
                listOf(
                    Server().url("https://api.voornameninliedjes.nl").description("Production Server"),
                    Server().url("http://localhost:8080").description("Local Server")
                )
            )
    }
}
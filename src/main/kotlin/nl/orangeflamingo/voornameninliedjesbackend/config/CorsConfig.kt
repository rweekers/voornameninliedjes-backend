package nl.orangeflamingo.voornameninliedjesbackend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "voornameninliedjes.cors.domains.allowed")
data class CorsConfig(
    val api: List<String>,
    val admin: List<String>
)
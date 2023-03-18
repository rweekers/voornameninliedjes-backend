package nl.orangeflamingo.voornameninliedjesbackend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration

@Configuration
@EnableWebMvc
class WebConfig(
    private val corsConfig: CorsConfig
) : WebMvcConfigurer {

    @Value("\${voornameninliedjes.images.path}")
    private val imagesPath: String = "images"

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*corsConfig.api.toTypedArray())
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true).maxAge(3600)

        registry.addMapping("/admin/**")
            .allowedOrigins(*corsConfig.admin.toTypedArray())
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true).maxAge(3600)
    }
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:${imagesPath}/")
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)))
    }
}
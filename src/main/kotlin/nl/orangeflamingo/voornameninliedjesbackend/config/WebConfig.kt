package nl.orangeflamingo.voornameninliedjesbackend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {

    @Value("\${voornameninliedjes.images.path}")
    private val imagesPath: String = "images"

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:${imagesPath}/")
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)))
    }
}
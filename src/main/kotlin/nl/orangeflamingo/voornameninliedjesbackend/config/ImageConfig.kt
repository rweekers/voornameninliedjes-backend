package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
@Profile("!integration-test")
class ImageConfig {

    @Value("\${voornameninliedjes.images.service.path}")
    private val imagesServicePath: String = "https://images.voornameninliedjes.nl"

    @Bean
    fun imageClient(builder: WebClient.Builder): ImageClient {
        val wca = WebClientAdapter.create(builder.baseUrl(imagesServicePath).build())
        return HttpServiceProxyFactory.builder()
            .exchangeAdapter(wca)
            .build()
            .createClient(ImageClient::class.java)
    }
}

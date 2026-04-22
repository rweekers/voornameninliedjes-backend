package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.client.ImageClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
@Profile("!integration-test")
class ImageConfig {

    @Value("\${voornameninliedjes.images.service.path}")
    private val imagesServicePath: String = "https://images.voornameninliedjes.nl"

    @Bean
    open fun imageClient(): ImageClient {
        val restClient = RestClient.builder()
            .baseUrl(imagesServicePath)
            .build()
        return HttpServiceProxyFactory.builder()
            .exchangeAdapter(RestClientAdapter.create(restClient))
            .build()
            .createClient(ImageClient::class.java)
    }
}

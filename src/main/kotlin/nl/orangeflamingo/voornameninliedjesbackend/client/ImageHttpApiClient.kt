package nl.orangeflamingo.voornameninliedjesbackend.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@Profile("!integration-test")
class ImageHttpApiClient(@Autowired val imageWebClient: WebClient) : ImageApiClient {
    override fun createBlurString(imageUrl: String, width: Int, height: Int): Mono<String> {
        return imageWebClient
            .get()
            .uri("?path=$imageUrl&width=$width&height=$height")
            .retrieve()
            .bodyToMono(String::class.java)
    }

    override fun downloadImage(imageUrl: String, filename: String, overwrite: Boolean): Mono<String> {
        return imageWebClient
            .post()
            .uri("?url=$imageUrl&filename=$filename&overwrite=$overwrite")
            .retrieve()
            .bodyToMono(String::class.java)
    }
}
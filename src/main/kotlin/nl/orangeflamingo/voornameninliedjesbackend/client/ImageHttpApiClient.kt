package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
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
            .bodyToMono(ImageHashDto::class.java)
            .map { it.hash }
    }

    override fun getDimensions(imageUrl: String): Mono<Pair<Int, Int>> {
        return imageWebClient
            .get()
            .uri("/dimensions?url=$imageUrl")
            .retrieve()
            .bodyToMono(ImageDimensionsDto::class.java)
            .map { Pair(it.width, it.height) }
    }

    override fun downloadImage(imageUrl: String, localPath: String, overwrite: Boolean): Mono<String> {
        return imageWebClient
            .post()
            .uri("?url=$imageUrl&filename=$localPath&overwrite=$overwrite")
            .retrieve()
            .bodyToMono(String::class.java)
    }
}
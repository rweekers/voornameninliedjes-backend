package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import reactor.core.publisher.Mono

class FakeImageClient: ImageClient {
    override fun createImageBlur(path: String, width: Int, height: Int): Mono<ImageHashDto> {
        return Mono.just(ImageHashDto("imageName", "imageHash"))
    }

    override fun getDimensions(url: String): Mono<ImageDimensionsDto> {
        return Mono.just(ImageDimensionsDto("imageName", 10, 10))
    }

    override fun downloadImage(url: String, filename: String, overwrite: Boolean): Mono<String> {
        return Mono.just("downloaded image")
    }
}
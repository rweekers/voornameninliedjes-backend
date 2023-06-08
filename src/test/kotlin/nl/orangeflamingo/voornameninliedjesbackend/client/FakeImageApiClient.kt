package nl.orangeflamingo.voornameninliedjesbackend.client

import reactor.core.publisher.Mono

class FakeImageApiClient: ImageApiClient {
    override fun createBlurString(imageUrl: String, width: Int, height: Int): Mono<String> {
        return Mono.just("blurredImage")
    }

    override fun getDimensions(imageUrl: String): Mono<Pair<Int, Int>> {
        return Mono.just(Pair(10, 10))
    }

    override fun downloadImage(imageUrl: String, localPath: String, overwrite: Boolean): Mono<String> {
        return Mono.just("downloaded image")
    }
}
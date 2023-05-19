package nl.orangeflamingo.voornameninliedjesbackend.client

import reactor.core.publisher.Mono

class FakeImageApiClient: ImageApiClient {
    override fun createBlurString(imageUrl: String, width: Int, height: Int): Mono<String> {
        return Mono.just("blurredImage")
    }
}
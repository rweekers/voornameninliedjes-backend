package nl.orangeflamingo.voornameninliedjesbackend.client

import reactor.core.publisher.Mono

interface ImageApiClient {

    fun createBlurString(imageUrl: String, width: Int, height: Int): Mono<String>

    fun downloadImage(artistImage: String, localPath: String): Mono<String>
}
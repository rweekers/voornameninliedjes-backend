package nl.orangeflamingo.voornameninliedjesbackend.client

import reactor.core.publisher.Mono

interface ImageApiClient {

    fun createBlurString(imageUrl: String, width: Int, height: Int): Mono<String>

    fun getDimensions(imageUrl: String): Mono<Pair<Int, Int>>

    fun downloadImage(imageUrl: String, localPath: String, overwrite: Boolean): Mono<String>
}
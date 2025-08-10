package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange
import reactor.core.publisher.Mono

interface ImageClient {

    @GetExchange
    fun createImageBlur(@RequestParam path: String, @RequestParam width: Int, @RequestParam height: Int): Mono<ImageHashDto>

    @GetExchange("/dimensions")
    fun getDimensions(@RequestParam url: String): Mono<ImageDimensionsDto>

    @PostExchange
    fun downloadImage(@RequestParam url: String, @RequestParam filename: String, @RequestParam overwrite: Boolean): Mono<String>
}
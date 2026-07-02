package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.PostExchange
import java.util.Optional

interface ImageClient {

    @GetExchange
    fun createImageBlur(@RequestParam path: String, @RequestParam width: Int, @RequestParam height: Int): Optional<ImageHashDto>

    @GetExchange("/dimensions")
    fun getDimensions(@RequestParam url: String): Optional<ImageDimensionsDto>

    @PostExchange
    fun downloadImage(@RequestParam url: String, @RequestParam filename: String, @RequestParam overwrite: Boolean): Optional<String>
}
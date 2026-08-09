package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import java.util.Optional

interface ImageClient {

    fun createImageBlur(path: String, width: Int, height: Int): Optional<ImageHashDto>

    fun getDimensions(url: String): Optional<ImageDimensionsDto>

    fun downloadImage(url: String, filename: String, overwrite: Boolean): Optional<String>
}
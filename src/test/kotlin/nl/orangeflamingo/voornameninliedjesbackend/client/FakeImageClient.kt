package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import java.util.Optional

class FakeImageClient: ImageClient {
    override fun createImageBlur(path: String, width: Int, height: Int): Optional<ImageHashDto> {
        return Optional.of(ImageHashDto("imageName", "imageHash"))
    }

    override fun getDimensions(url: String): Optional<ImageDimensionsDto> {
        return Optional.of(ImageDimensionsDto("imageName", 10, 10))
    }

    override fun downloadImage(url: String, filename: String, overwrite: Boolean): Optional<String> {
        return Optional.of("downloaded image")
    }
}
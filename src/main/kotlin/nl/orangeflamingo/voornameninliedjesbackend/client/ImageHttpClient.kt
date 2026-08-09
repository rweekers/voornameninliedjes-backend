package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestClient
import java.util.Optional

class ImageHttpClient(
    @param:Autowired val imageRestClient: RestClient,
) : ImageClient {

    override fun createImageBlur(
        path: String,
        width: Int,
        height: Int
    ): Optional<ImageHashDto> {
        val result = imageRestClient.get()
            .uri { builder ->
                builder
                    .queryParam("path", path)
                    .queryParam("width", width)
                    .queryParam("height", height)
                    .build()
            }
            .retrieve()
            .body(ImageHashDto::class.java)
        return Optional.ofNullable(result)
    }

    override fun getDimensions(url: String): Optional<ImageDimensionsDto> {
        val result = imageRestClient.get()
            .uri { builder ->
                builder
                    .path("/dimensions")
                    .queryParam("url", url)
                    .build()
            }
            .retrieve()
            .body(ImageDimensionsDto::class.java)
        return Optional.ofNullable(result)
    }

    override fun downloadImage(
        url: String,
        filename: String,
        overwrite: Boolean
    ): Optional<String> {
        val result = imageRestClient.post()
            .uri { builder ->
                builder
                    .queryParam("url", url)
                    .queryParam("filename", filename)
                    .queryParam("overwrite", overwrite)
                    .build()
            }
            .retrieve()
            .body(String::class.java)
        return Optional.ofNullable(result)
    }
}
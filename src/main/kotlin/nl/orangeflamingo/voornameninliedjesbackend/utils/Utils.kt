package nl.orangeflamingo.voornameninliedjesbackend.utils

import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.net.URL


object Utils {

    fun resourceAsInputStream(fileName: String): URL {
        val resourceLoader: ResourceLoader = DefaultResourceLoader()
        val resource: Resource = resourceLoader.getResource(fileName)
        return resource.url
    }
}
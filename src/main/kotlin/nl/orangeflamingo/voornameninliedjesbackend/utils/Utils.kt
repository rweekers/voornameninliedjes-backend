package nl.orangeflamingo.voornameninliedjesbackend.utils

import io.github.furstenheim.CopyDown
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.net.URL


object Utils {

    private val converter = CopyDown()

    fun resourceAsInputStream(fileName: String): URL {
        val resourceLoader: ResourceLoader = DefaultResourceLoader()
        val resource: Resource = resourceLoader.getResource(fileName)
        return resource.url
//        return Utils.javaClass.getResource(fileName)
    }

    fun html2md(input: String?): String {
        if (input == null) return ""
        return converter.convert(input)
    }

    fun cleanString(input: String): String {
        return input.replace(Regex("[#\\[\\]]"), "")
    }
}
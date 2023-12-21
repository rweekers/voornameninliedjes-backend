package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference

class MyRuntimeHints: RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        // Register serialization
        hints.serialization().registerType(TypeReference.of(ImageDimensionsDto::class.java))
        hints.serialization().registerType(TypeReference.of(ImageHashDto::class.java))
    }
}
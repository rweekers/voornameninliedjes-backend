package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmAlbumDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmArtistDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmResponseDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTagDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTopTagsDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrackDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmWikiDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageDimensionsDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.ImageHashDto
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference

class MyRuntimeHints: RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        // Register serialization
        hints.reflection().registerType(TypeReference.of(ImageDimensionsDto::class.java))
        hints.reflection().registerType(TypeReference.of(ImageHashDto::class.java))
        hints.reflection().registerType(TypeReference.of(LastFmResponseDto::class.java))
        hints.reflection().registerType(TypeReference.of(LastFmTrackDto::class.java))
        hints.reflection().registerType(TypeReference.of(LastFmArtistDto::class.java))
        hints.reflection().registerType(TypeReference.of(LastFmAlbumDto::class.java))
        hints.reflection().registerType(TypeReference.of(LastFmTopTagsDto::class.java))
        hints.reflection().registerType(TypeReference.of(LastFmTagDto::class.java))
        hints.reflection().registerType(TypeReference.of(LastFmWikiDto::class.java))
    }
}
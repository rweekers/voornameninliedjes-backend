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
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

class MyRuntimeHints: RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        // Register serialization
        hints.registerDto(ImageDimensionsDto::class.java)
        hints.registerDto(ImageHashDto::class.java)
        hints.registerDto(LastFmResponseDto::class.java)
        hints.registerDto(LastFmTrackDto::class.java)
        hints.registerDto(LastFmArtistDto::class.java)
        hints.registerDto(LastFmAlbumDto::class.java)
        hints.registerDto(LastFmTopTagsDto::class.java)
        hints.registerDto(LastFmTagDto::class.java)
        hints.registerDto(LastFmWikiDto::class.java)
    }

    private fun RuntimeHints.registerDto(type: Class<*>) {
        reflection().registerType(
            type,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
        )
    }
}
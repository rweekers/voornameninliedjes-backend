package nl.orangeflamingo.voornameninliedjesbackend.controller

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class WikipediaLanguageConverter : Converter<String, WikipediaLanguage> {

    override fun convert(source: String): WikipediaLanguage =
        WikipediaLanguage.valueOf(source.uppercase())

}
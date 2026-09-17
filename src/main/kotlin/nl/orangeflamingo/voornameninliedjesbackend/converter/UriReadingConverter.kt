package nl.orangeflamingo.voornameninliedjesbackend.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.net.URI

@ReadingConverter
class UriReadingConverter : Converter<String, URI> {
    override fun convert(source: String): URI = URI.create(source)
}
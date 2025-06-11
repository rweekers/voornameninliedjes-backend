package nl.orangeflamingo.voornameninliedjesbackend.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.net.URI

@WritingConverter
class UriWritingConverter : Converter<URI, String> {
    override fun convert(source: URI): String = source.toString()
}
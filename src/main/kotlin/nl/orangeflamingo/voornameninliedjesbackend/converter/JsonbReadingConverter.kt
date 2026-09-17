package nl.orangeflamingo.voornameninliedjesbackend.converter

import nl.orangeflamingo.voornameninliedjesbackend.domain.Jsonb
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class JsonbReadingConverter : Converter<PGobject, Jsonb> {

    override fun convert(source: PGobject): Jsonb =
        Jsonb(source.value ?: "")
}
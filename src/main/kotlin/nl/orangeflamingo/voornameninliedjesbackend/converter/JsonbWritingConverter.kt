package nl.orangeflamingo.voornameninliedjesbackend.converter

import nl.orangeflamingo.voornameninliedjesbackend.domain.Jsonb
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.core.mapping.JdbcValue
import java.sql.JDBCType

@WritingConverter
class JsonbWritingConverter : Converter<Jsonb, JdbcValue> {

    override fun convert(source: Jsonb): JdbcValue =
        JdbcValue.of(source.value, JDBCType.OTHER)
}
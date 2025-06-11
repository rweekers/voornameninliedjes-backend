package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.converter.UriReadingConverter
import nl.orangeflamingo.voornameninliedjesbackend.converter.UriWritingConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class JdbcConfig: AbstractJdbcConfiguration() {

    override fun userConverters(): List<Any> {
        return listOf<Any>(UriReadingConverter(), UriWritingConverter())
    }
}
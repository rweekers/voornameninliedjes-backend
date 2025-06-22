package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.config.MyRuntimeHints
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ImportRuntimeHints(MyRuntimeHints::class)
@ConfigurationPropertiesScan
@EnableCaching
class SongApplication

fun main(args: Array<String>) {
    runApplication<SongApplication>(*args)
}
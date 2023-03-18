package nl.orangeflamingo.voornameninliedjesbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
class SongApplication

fun main(args: Array<String>) {
    runApplication<SongApplication>(*args)
}
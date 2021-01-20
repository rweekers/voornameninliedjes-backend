package nl.orangeflamingo.voornameninliedjesbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class SongApplication

fun main(args: Array<String>) {
    runApplication<SongApplication>(*args)
}
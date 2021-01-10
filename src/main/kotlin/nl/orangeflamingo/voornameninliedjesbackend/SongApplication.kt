package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoSongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootApplication
@EnableScheduling
class SongApplication {

    private val log = LoggerFactory.getLogger(SongApplication::class.java)

    @Bean
    @Profile("dev")
    fun init(repository: MongoSongRepository, userRepository: UserRepository, passwordEncoder: PasswordEncoder) = CommandLineRunner {
        val wikimediaPhotoPaulSimon = WikimediaPhoto(
            "https://upload.wikimedia.org/wikipedia/commons/2/2d/Paul_Simon_in_1982.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/2/2d/Paul_Simon_in_1982.jpg"
        )
        val wikimediaPhotoMichaelJackson = WikimediaPhoto(
            "https://upload.wikimedia.org/wikipedia/commons/3/31/Michael_Jackson_in_1988.jpg",
            "Zoran Veselinovic, CC BY-SA 2.0 <https://creativecommons.org/licenses/by-sa/2.0>, via Wikimedia Commons"
        )
        val wikimediaPhotoMichaelJackson2 = WikimediaPhoto(
            "https://upload.wikimedia.org/wikipedia/commons/0/04/Michael_Jackson_1984.jpg",
            "White House Photo Office, Public domain, via Wikimedia Commons"
        )
        val wikimediaPhotoThePolice = WikimediaPhoto(
            "https://upload.wikimedia.org/wikipedia/commons/f/ff/Sting2.jpg",
            "Rita Molnár, CC BY-SA 2.5 <https://creativecommons.org/licenses/by-sa/2.5>, via Wikimedia Commons"
        )
        val sourceYouCanCallMeAl = Source("https://nl.wikipedia.org/wiki/You_Can_Call_Me_Al", "Wikipedia over 'You Can Call Me Al'")
        val sourceSweetCaroline = Source("https://nl.wikipedia.org/wiki/Sweet_Caroline", "Wikipedia over 'Sweet Caroline'")
        val sourceSweetCaroline2 = Source("https://www.nporadio2.nl/song/21689/sweet-caroline", "NPO Radio2 over Sweet Caroline")

        val songList = listOf(
            DbSong(
                "1",
                "Michael Jackson",
                "Ben",
                "Ben",
                "Background on Ben",
                "s0u8iA6bJTo",
                "3vZMkLS1jP7NdNhzqGfUSW",
                null,
                setOf(wikimediaPhotoMichaelJackson),
                setOf(),
                setOf(),
                listOf(),
                SongStatus.SHOW
            ),
            DbSong(
                "2",
                "Michael Jackson",
                "Dirty Diana",
                "Diana",
                "Background on Dirty Diana",
                "yUi_S6YWjZw",
                "6JZYMxvcoeLD4IifJPvDux",
                null,
                setOf(wikimediaPhotoMichaelJackson, wikimediaPhotoMichaelJackson2),
                setOf(),
                setOf(),
                listOf(),
                SongStatus.SHOW
            ),
            DbSong(
                "3",
                "Neil Diamond",
                "Sweet Caroline",
                "Caroline",
                "Background on Sweet Caroline",
                "1vhFnTjia_I",
                "62AuGbAkt8Ox2IrFFb8GKV",
                null,
                setOf(),
                setOf("49108851478", "2623883171"),
                setOf(sourceSweetCaroline, sourceSweetCaroline2),
                listOf(
                    LogEntry(Instant.now(), "Remco")
                ),
                SongStatus.SHOW
            ),
            DbSong(
                "4",
                "The Police",
                "Roxanne",
                "Roxanne",
                "Background on Roxanne",
                "3T1c7GkzRQQ",
                "3EYOJ48Et32uATr9ZmLnAo",
                null,
                setOf(wikimediaPhotoThePolice),
                setOf(),
                setOf(),
                listOf(),
                SongStatus.SHOW
            ),
            DbSong(
                "5",
                "Dolly Parton",
                "Jolene",
                "Jolene",
                null,
                null,
                null,
                null,
                setOf(),
                setOf(),
                setOf(),
                listOf(),
                SongStatus.IN_PROGRESS
            ),
            DbSong(
                "6",
                "The Kinks",
                "Lola",
                "Lola",
                null,
                null,
                null,
                null,
                setOf(),
                setOf(),
                setOf(),
                listOf(),
                SongStatus.IN_PROGRESS
            ),
            DbSong(
                "7",
                "Paul Simon",
                "You Can Call Me Al",
                "Al",
                "Background on You Can Call Me All",
                "uq-gYOrU8bA",
                "0qxYx4F3vm1AOnfux6dDxP",
                null,
                setOf(wikimediaPhotoPaulSimon),
                setOf("5919550669"),
                setOf(sourceYouCanCallMeAl),
                listOf(
                    LogEntry(Instant.now().minus(1, ChronoUnit.DAYS), "Parser"),
                    LogEntry(Instant.now(), "Remco")
                ),
                SongStatus.SHOW
            )
        )
        repository.saveAll(songList)
    }
}

fun main(args: Array<String>) {
    runApplication<SongApplication>(*args)
}
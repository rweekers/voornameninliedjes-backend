package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.domain.DbSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.Source
import nl.orangeflamingo.voornameninliedjesbackend.domain.WikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.repository.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import reactor.core.publisher.Flux

@SpringBootApplication
@EnableScheduling
class SongApplication {

	private val log = LoggerFactory.getLogger(SongApplication::class.java)

	@Bean
	@Profile("dev")
	fun init(repository: SongRepository) = CommandLineRunner {
		val wikimediaPhoto = WikimediaPhoto("https://upload.wikimedia.org/wikipedia/commons/2/2d/Paul_Simon_in_1982.jpg", "https://upload.wikimedia.org/wikipedia/commons/2/2d/Paul_Simon_in_1982.jpg")
		val source = Source("https://nl.wikipedia.org/wiki/You_Can_Call_Me_Al", "Wikipedia over 'You Can Call Me Al'")
		val source2 = Source("https://nl.wikipedia.org/wiki/Sweet_Caroline", "Wikipedia over 'Sweet Caroline'")

		val songFlux = Flux.just(
			DbSong("1", "Michael Jackson", "Ben", "Ben", "Background on Ben", "s0u8iA6bJTo", "3vZMkLS1jP7NdNhzqGfUSW", null, setOf(wikimediaPhoto), setOf(), setOf(), listOf(), SongStatus.SHOW),
			DbSong("2", "Neil Diamond", "Sweet Caroline", "Caroline", "Background on Sweet Caroline", "1vhFnTjia_I", "62AuGbAkt8Ox2IrFFb8GKV", null, setOf(), setOf("49108851478", "2623883171"), setOf(source2), listOf(), SongStatus.SHOW),
			DbSong("3", "The Police", "Roxanne", "Roxanne", "Background on Roxanne", "3T1c7GkzRQQ", "3EYOJ48Et32uATr9ZmLnAo", null, setOf(wikimediaPhoto), setOf(), setOf(), listOf(), SongStatus.SHOW),
			DbSong("4", "Dolly Parton", "Jolene", "Jolene", null, null, null, null, setOf(), setOf(), setOf(), listOf(), SongStatus.IN_PROGRESS),
			DbSong("5", "The Kinks", "Lola", "Lola", null, null, null, null, setOf(), setOf(), setOf(), listOf(), SongStatus.IN_PROGRESS),
			DbSong("6", "Paul Simon", "You Can Call Me Al", "Al", "Background on You Can Call Me All", "uq-gYOrU8bA", "0qxYx4F3vm1AOnfux6dDxP", null, setOf(wikimediaPhoto), setOf("5919550669"), setOf(source), listOf(), SongStatus.SHOW)
		)

		repository.saveAll(songFlux).thenMany<DbSong>{ repository.findAll() }.subscribe{ song -> log.info(song.toString()) }
	}
}

fun main(args: Array<String>) {
	runApplication<SongApplication>(*args)
}
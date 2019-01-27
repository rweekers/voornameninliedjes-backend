package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.domain.Audit
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import reactor.core.publisher.Flux
import java.time.Instant

@SpringBootApplication
class SongApplication {

	private val log = LoggerFactory.getLogger(SongApplication::class.java)

	@Bean
	@Profile("!pro")
	fun init(repository: SongRepository) = CommandLineRunner {
//			val mongo = MongoClient()//("localhost", 27017);
//			val template = MongoTemplate(mongo, "local")
//			val songCollectionName = template.getCollectionName(Song::class.java)
//
//			if (template.collectionExists(songCollectionName)) {
//				template.dropCollection(songCollectionName)
//			}
//
//			// Capped collections need to be created manually
//			template.createCollection(songCollectionName, CollectionOptions.empty().capped().size(9999999L))//.maxDocuments(100L));

			val songFlowable = Flux.just(
                    Song("1", "Michael Jackson", "Ben", "Ben", null, null, SongStatus.SHOW, Audit(dateInserted = Instant.now(), userInserted =  "Remco")),
                    Song("2", "Neil Diamond", "Sweet Caroline", "Caroline", null, null, SongStatus.SHOW, Audit(dateInserted = Instant.now(), userInserted =  "Remco")),
                    Song("3", "The Police", "Roxanne", "Roxanne", null, null, SongStatus.SHOW, Audit(dateInserted = Instant.now(), userInserted =  "Remco")),
                    Song("4", "Dolly Parton", "Jolene", "Jolene", null, null, SongStatus.IN_PROGRESS, Audit(dateInserted = Instant.now(), userInserted =  "Remco")),
                    Song("5", "The Kinks", "Lola", "Lola", null, null, SongStatus.IN_PROGRESS, Audit(dateInserted = Instant.now(), userInserted =  "Remco"))
			)

			repository.saveAll(songFlowable).thenMany<Song>{ repository.findAll() }.subscribe{ song -> log.info(song.toString()) }
	}
}

fun main(args: Array<String>) {
	runApplication<SongApplication>(*args)
}
package nl.orangeflamingo.voornameninliedjesbackend

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.repository.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import reactor.core.publisher.Flux

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
					Song("1", "Michael Jackson", "Ben", "Ben"),
					Song("2", "Neil Diamond", "Sweet Caroline", "Caroline"),
					Song("3", "The Police", "Roxanne", "Roxanne"),
					Song("4", "Dolly Parton", "Jolene", "Jolene"),
					Song("5", "The Kinks", "Lola", "Lola")
			)

			repository.saveAll(songFlowable).thenMany<Song>{ repository.findAll() }.subscribe{ song -> log.info(song.toString()) }
	}
}

fun main(args: Array<String>) {
	runApplication<SongApplication>(*args)
}
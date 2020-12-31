package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.domain.MongoSong
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["nl.orangeflamingo.voornameninliedjesbackend.repository.mongo"])
class MongoConfig {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var mongoConverter: MongoConverter

    @EventListener(ApplicationReadyEvent::class)
    fun initIndicesAfterStartup() {
        val indexOps = mongoTemplate.indexOps(MongoSong::class.java)
        val resolver = MongoPersistentEntityIndexResolver(mongoConverter.mappingContext)
        resolver.resolveIndexFor(MongoSong::class.java).forEach { indexOps.ensureIndex(it) }
    }
}
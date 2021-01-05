package nl.orangeflamingo.voornameninliedjesbackend.repository.mongo

import nl.orangeflamingo.voornameninliedjesbackend.domain.MongoUser
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoUserRepository : MongoRepository<MongoUser, String> {
    fun findByUsername(username: String): MongoUser?
}
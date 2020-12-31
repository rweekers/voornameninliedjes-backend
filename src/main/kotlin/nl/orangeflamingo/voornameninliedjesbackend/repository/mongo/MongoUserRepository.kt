package nl.orangeflamingo.voornameninliedjesbackend.repository.mongo

import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoUserRepository : MongoRepository<User, String> {
    fun findByUsername(username: String): User?
}
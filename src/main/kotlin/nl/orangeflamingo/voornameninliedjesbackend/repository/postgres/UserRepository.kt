package nl.orangeflamingo.voornameninliedjesbackend.repository.postgres

import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, String> {
    fun findByUsername(username: String): User?
}
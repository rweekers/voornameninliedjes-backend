package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("users")
class User(

    @Id
    var id: Long? = null,
    val username: String,
    val password: String,
    @MappedCollection(idColumn = "user_id")
    val roles: MutableSet<UserRole>
)

@Table("user_roles")
data class UserRole(
    val name: String
)
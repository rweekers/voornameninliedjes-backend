package nl.orangeflamingo.voornameninliedjesbackend.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(

    @Id
    var id: Long? = null,
    val username: String,
    val password: String,
    @MappedCollection(idColumn = "user_id", keyColumn = "id")
    val roles: MutableList<UserRole>
)

@Table("user_roles")
data class UserRole(
    @Id
    var id: Long? = null,
    val name: String
)
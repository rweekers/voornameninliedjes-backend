package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.controller.Utils.Companion.INVALID_CREDENTIALS
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoUserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/beta")
class MongoUserController {
    private val log = LoggerFactory.getLogger(MongoUserController::class.java)

    @Autowired
    private lateinit var userRepository: MongoUserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/admin/users")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun getUsers(): List<UserDto> {
        log.info("Requesting all users")
        return userRepository.findAll().map { convertToDto(it) }
    }

    @PostMapping("/authenticate")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun authenticate(@RequestBody user: UserDto): ResponseEntity<UserDto> {
        log.info("Authenticating user ${user.username}")
        val dbUser = userRepository.findByUsername(username = user.username)
            ?: throw InvalidCredentialsException(INVALID_CREDENTIALS)
        if (!passwordEncoder.matches(user.password, dbUser.password)) {
            throw InvalidCredentialsException(INVALID_CREDENTIALS)
        }

        return ResponseEntity.of(
            Optional.of(
                UserDto(
                    id = dbUser.id,
                    username = dbUser.username,
                    password = user.password,
                    roles = dbUser.roles
                )
            )
        )
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(exception: InvalidCredentialsException): ResponseEntity<ResponseError> {
        return ResponseEntity(ResponseError(exception.message ?: "Onbekende fout"), HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exception: UserNotFoundException): ResponseEntity<ResponseError> {
        return ResponseEntity(ResponseError(exception.message ?: "Onbekende fout"), HttpStatus.BAD_REQUEST)
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/users/{id}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun getUserById(@PathVariable("id") id: String): UserDto {
        log.info("Requesting user with id $id")
        return userRepository.findById(id).map { convertToDto(it) }.orElseThrow()
    }

    private fun convertToDto(user: User): UserDto {
        return UserDto(user.id, user.username, user.password, user.roles)
    }
}

data class UserDto(

    val id: String? = null,

    val username: String,

    val password: String,

    val roles: MutableSet<String> = mutableSetOf()
)

class InvalidCredentialsException(message: String) : Exception(message)

class UserNotFoundException(message: String) : Exception(message)

class Utils {
    companion object {
        const val INVALID_CREDENTIALS = "Gebruikersnaam en/of wachtwoord onjuist"
    }
}

data class ResponseError(val message: String)
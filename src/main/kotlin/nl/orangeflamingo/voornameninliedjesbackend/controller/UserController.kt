package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.controller.UserControllerUtils.Companion.INVALID_CREDENTIALS
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.domain.UserRole
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RestController
@RequestMapping("/admin")
class UserController(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {
    private val log = LoggerFactory.getLogger(UserController::class.java)

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping(value = ["/users", "/users/"])
    fun getUsers(): List<UserDto> {
        log.info("Requesting all users")
        return userRepository.findAll().map { convertToDto(it) }
    }

    @PostMapping("/authenticate")
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
                    id = dbUser.id.toString(),
                    username = dbUser.username,
                    password = user.password,
                    roles = dbUser.roles.map { it.name }.toMutableSet()
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
    fun getUserById(@PathVariable("id") id: String): UserDto {
        log.info("Requesting user with id ${id.replace("[\n\r]".toRegex(), "_")}")
        return userRepository.findById(id).map { convertToDto(it) }
            .orElseThrow { UserNotFoundException("User with id $id not found") }
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PostMapping("/users")
    fun newUser(@RequestBody newUser: UserDto): User {
        log.info("Requesting creation of new user ${newUser.username}")
        val user = convert(newUser)
        return userRepository.save(user)
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/users/{userId}")
    fun deleteUser(@PathVariable userId: String) {
        log.info("Deleting user with id ${userId.replace("[\n\r]".toRegex(), "_")}")
        userRepository.deleteById(userId)
    }

    private fun convert(userDto: UserDto): User {
        return User(
            userDto.id?.toLong(),
            userDto.username,
            passwordEncoder.encode(userDto.password),
            userDto.roles.map { UserRole(null, it) }.toMutableSet()
        )
    }

    private fun convertToDto(user: User): UserDto {
        return UserDto(user.id.toString(), user.username, user.password, user.roles.map { it.name }.toMutableSet())
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

class UserControllerUtils {
    companion object {
        const val INVALID_CREDENTIALS = "Gebruikersnaam en/of wachtwoord onjuist"
    }
}

data class ResponseError(val message: String)
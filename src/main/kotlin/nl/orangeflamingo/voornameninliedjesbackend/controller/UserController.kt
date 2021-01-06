package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.controller.Utils.Companion.INVALID_CREDENTIALS
import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import nl.orangeflamingo.voornameninliedjesbackend.domain.UserRole
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/delta")
class UserController {
    private val log = LoggerFactory.getLogger(UserController::class.java)

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/users")
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
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun getUserById(@PathVariable("id") id: String): UserDto {
        log.info("Requesting user with id $id")
        return userRepository.findById(id).map { convertToDto(it) }.orElseThrow()
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @PostMapping("/users")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun newUser(@RequestBody newUser: UserDto): User {
        log.info("Requesting creation of new user ${newUser.username}")
        val user = convert(newUser)
        return userRepository.save(user)
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @DeleteMapping("/users/{userId}")
    @CrossOrigin(origins = ["http://localhost:3000", "https://voornameninliedjes.nl", "*"])
    fun deleteUser(@PathVariable userId: String) {
        log.info("Deleting user with id $userId")
        userRepository.deleteById(userId)
    }

    private fun convert(userDto: UserDto): User {
        return User(
            userDto.id?.toLong(),
            userDto.username,
            passwordEncoder.encode(userDto.password),
            userDto.roles.map { UserRole(it) }.toMutableSet()
        )
    }

    private fun convertToDto(user: User): UserDto {
        return UserDto(user.id.toString(), user.username, user.password, user.roles.map { it.name }.toMutableSet())
    }
}
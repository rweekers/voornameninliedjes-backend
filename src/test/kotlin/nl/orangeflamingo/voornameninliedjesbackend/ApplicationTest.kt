package nl.orangeflamingo.voornameninliedjesbackend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("smoke-test")
class ApplicationTest {

    @Test
    fun contextLoads() {
    }
}
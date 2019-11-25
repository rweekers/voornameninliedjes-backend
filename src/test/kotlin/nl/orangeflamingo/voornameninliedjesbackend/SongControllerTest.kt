package nl.orangeflamingo.voornameninliedjesbackend

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SongControllerTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	fun testGetAllSongs() {
		this.mockMvc.get("/api/songs").andExpect { status { `is`(200) } }
	}

}


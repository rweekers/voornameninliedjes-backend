package nl.orangeflamingo.voornameninliedjesbackend.controller

import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SongController {

    @GetMapping("/songs/")
    fun getSongs() =
            listOf(Song("test", "name", "Hello, name", "test2"))

    @GetMapping("/songs/{id}")
    fun getSongById(@PathVariable("id") id: String) =
            Song(id, "bla", "die bla", "bla2")
}
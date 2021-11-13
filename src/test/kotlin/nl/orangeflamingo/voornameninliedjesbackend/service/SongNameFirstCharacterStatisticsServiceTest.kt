package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.SongNameFirstCharacterStatistics
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongNameFirstCharacterStatisticsRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SongNameFirstCharacterStatisticsServiceTest {

    private val songNameFirstCharacterStatisticsRepository = mock(SongNameFirstCharacterStatisticsRepository::class.java)
    private val songRepository = mock(SongRepository::class.java)

    private val songNameFirstCharacterStatisticsService: SongNameFirstCharacterStatisticsService = SongNameFirstCharacterStatisticsService(
        songNameFirstCharacterStatisticsRepository, songRepository
    )

    private val simpleSongNameStatistics = listOf(
        SongNameFirstCharacterStatistics("a", 5),
        SongNameFirstCharacterStatistics("b", 2),
        SongNameFirstCharacterStatistics("c", 1),
        SongNameFirstCharacterStatistics("d", 1),
        SongNameFirstCharacterStatistics("e", 3),
        SongNameFirstCharacterStatistics("f", 1)
    )
    private val fullSongNameStatistics = listOf(
        SongNameFirstCharacterStatistics("a",  7),
        SongNameFirstCharacterStatistics("b",  8),
        SongNameFirstCharacterStatistics("c",  7),
        SongNameFirstCharacterStatistics("d",  6),
        SongNameFirstCharacterStatistics("e",  8),
        SongNameFirstCharacterStatistics("f",  2),
        SongNameFirstCharacterStatistics("g",  3),
        SongNameFirstCharacterStatistics("h",  2),
        SongNameFirstCharacterStatistics("i",  1),
        SongNameFirstCharacterStatistics("j", 11),
        SongNameFirstCharacterStatistics("k",  2),
        SongNameFirstCharacterStatistics("l",  5),
        SongNameFirstCharacterStatistics("m", 10),
        SongNameFirstCharacterStatistics("n",  3),
        SongNameFirstCharacterStatistics("o",  1),
        SongNameFirstCharacterStatistics("p",  3),
        SongNameFirstCharacterStatistics("q",  1),
        SongNameFirstCharacterStatistics("r",  7),
        SongNameFirstCharacterStatistics("s",  8),
        SongNameFirstCharacterStatistics("t",  2),
        SongNameFirstCharacterStatistics("u",  1),
        SongNameFirstCharacterStatistics("v",  2),
        SongNameFirstCharacterStatistics("w",  1),
        SongNameFirstCharacterStatistics("y",  0),
        SongNameFirstCharacterStatistics("z",  1)
    )

    @ParameterizedTest(name = "{index} - Met een max size van {0} worden {1} pagina's verwacht")
    @MethodSource("provideMaxSize")
    fun divideSongs(maxSize: Int, expectedPages: Int, songNameStatistics: List<SongNameFirstCharacterStatistics>) {
        `when`(songNameFirstCharacterStatisticsRepository.findAllStatusShowGroupedByNameStartingWithOrderedByName()).thenReturn(
            songNameStatistics
        )
        `when`(songRepository.count()).thenReturn(songNameStatistics.sumOf { it.count }.toLong())
        val redividedList = songNameFirstCharacterStatisticsService.redivide(maxSize)
        assertEquals(expectedPages, redividedList.size)
    }

    private fun provideMaxSize(): Stream<Arguments> {
        return Stream.of(
            arguments(5, 3, simpleSongNameStatistics),
            arguments(10, 2, simpleSongNameStatistics),
            arguments(20, 7, fullSongNameStatistics)
        )
    }
}
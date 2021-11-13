package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.SongNameFirstCharacterStatistics
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongNameFirstCharacterStatisticsRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SongNameFirstCharacterStatisticsService @Autowired constructor(
    val songNameFirstCharacterStatisticsRepository: SongNameFirstCharacterStatisticsRepository,
    val songRepository: SongRepository
) {
    private val log = LoggerFactory.getLogger(SongNameFirstCharacterStatisticsService::class.java)

    fun redivide(maxSize: Int): List<SongNameFirstCharacterStatistics> {
        val songNameStatistics =
            songNameFirstCharacterStatisticsRepository.findAllStatusShowGroupedByNameStartingWithOrderedByName()

        // check total and average size
        val total = songRepository.count()
        val averageSizePerCharacter = kotlin.math.ceil(total.toDouble() / 26)

        val size = if (averageSizePerCharacter >= 20) {
            (kotlin.math.ceil(26.toDouble() / (total.toDouble() / 20))).toInt()
        } else {
            maxSize
        }

        val redividedStatistics =
            songNameStatistics.fold(mutableListOf<SongNameFirstCharacterStatistics>()) { acc, curr -> divide(curr, acc, size) }
        log.info("Divided {} statistics into {} total", songNameStatistics.size, redividedStatistics.size)
        return redividedStatistics
    }

    private fun divide(
        songNameStatistics: SongNameFirstCharacterStatistics,
        dividedSongs: MutableList<SongNameFirstCharacterStatistics>,
        maxSize: Int
    ): MutableList<SongNameFirstCharacterStatistics> {
        if (dividedSongs.isEmpty()) {
            dividedSongs.add(songNameStatistics)
            return dividedSongs
        }
        val last = dividedSongs.last()
        if (last.count + songNameStatistics.count <= maxSize) {
            last.count += songNameStatistics.count
            last.name += songNameStatistics.name
        } else {
            dividedSongs.add(songNameStatistics)
        }
        return dividedSongs
    }
}

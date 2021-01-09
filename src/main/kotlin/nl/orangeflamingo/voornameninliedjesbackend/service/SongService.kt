package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoSongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class SongService @Autowired constructor(
    val artistRepository: ArtistRepository,
    val songRepository: SongRepository,
    val mongoSongRepository: MongoSongRepository
) {
    private val log = LoggerFactory.getLogger(SongService::class.java)

    fun countSongs(): Long {
        return songRepository.count()
    }

    fun migrateSongs() {
        val songs = mongoSongRepository.findAll()
        songs.forEach { migrateSong(it) }
        log.info("Gotten ${songs.size} songs")
    }

    private fun migrateSong(mongoSong: DbSong) {
        val artist = artistRepository.findFirstByName(mongoSong.artist) ?: artistRepository.save(
            Artist(
                name = mongoSong.artist,
                background = null,
                wikimediaPhotos = mutableSetOf(),
                flickrPhotos = mutableSetOf(),
                logEntries = mutableListOf(
                    ArtistLogEntry(
                        date = Instant.now(),
                        username = "Mongo2PostgresMigration"
                    )
                )
            )
        )

        val song = Song(
            title = mongoSong.title,
            name = mongoSong.name,
            artistImage = mongoSong.artistImage,
            background = mongoSong.background,
            youtube = mongoSong.youtube,
            spotify = mongoSong.spotify,
            status = mongoSong.status ?: SongStatus.IN_PROGRESS,
            mongoId = mongoSong.id,
            sources = mongoSong.sources.map {
                SongSource(
                    url = it.url,
                    name = it.name
                )
            }.toMutableList(),
            logEntries = mongoSong.logs.map {
                SongLogEntry(
                    date = it.date,
                    username = it.user
                )
            }.toMutableList()
        )

        song.addArtist(artist)

        val songD = songRepository.save(song)

        val artistD = updateArtist(mongoSong, artist)

        log.info("Migrated $mongoSong, resulted in $songD and $artistD")
    }

    private fun updateArtist(song: DbSong, artist: Artist): Artist {
        song.wikimediaPhotos.forEach {
            artist.addWikimediaPhoto(
                ArtistWikimediaPhoto(
                    url = it.url,
                    attribution = it.attribution
                )
            )
        }
        song.flickrPhotos.forEach {
            artist.addFlickrPhoto(
                ArtistFlickrPhoto(
                    flickrId = it
                )
            )
        }
        return artistRepository.save(artist)
    }
}
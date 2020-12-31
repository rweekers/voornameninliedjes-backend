package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoSongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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

    private fun migrateSong(song: DbSong) {
        val artist = artistRepository.findFirstByName(song.artist)!!

        val pgSong = Song(
            title = song.title,
            name = song.name,
            artistId = artist.id!!,
            artistImage = song.artistImage,
            background = song.background,
            youtube = song.youtube,
            spotify = song.spotify,
            status = song.status ?: SongStatus.IN_PROGRESS,
            mongoId = song.id,
            sources = song.sources.map {
                SongSource(
                    url = it.url,
                    name = it.name
                )
            }.toMutableList(),
            logEntries = song.logs.map {
                SongLogEntry(
                    date = it.date,
                    username = it.user
                )
            }.toMutableList()
        )
        val songD = songRepository.save(pgSong)

        val artistD = updateArtist(song, artist)

        log.info("Migrated $song, resulted in $songD and $artistD")
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


        // Mongo2PostMigration

        return artistRepository.save(artist)
    }
}
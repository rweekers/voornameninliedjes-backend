package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoSongRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class SongService @Autowired constructor(
    val artistRepository: ArtistRepository,
    val mongoSongRepository: MongoSongRepository,
    val songRepository: SongRepository,
    val flickrApiClient: FlickrApiClient
) {
    private val log = LoggerFactory.getLogger(SongService::class.java)

    fun countSongs(): Long {
        return songRepository.count()
    }

    fun findAll(): List<AggregateSong> {
        return songRepository.findAll()
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()
                createAggregateSong(song, artist)
            }
    }


    fun findAllByStatusOrderedByName(status: SongStatus): List<AggregateSong> {
        return songRepository.findAllByStatusOrderedByName(status)
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()
                createAggregateSong(song, artist)
            }
    }

    fun findById(id: Long): AggregateSong {
        log.info("Getting song with id $id")
        val song = songRepository.findById(id).orElseThrow()
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()

        return createAggregateSong(song, artist)
    }

    private fun createAggregateSong(
        song: Song,
        artist: Artist
    ) = AggregateSong(
        id = song.id ?: throw RuntimeException(),
        title = song.title,
        name = song.name,
        artistName = artist.name,
        background = song.background,
        youtube = song.youtube,
        spotify = song.spotify,
        status = song.status,
        artistImage = song.artistImage,
        wikimediaPhotos = artist.wikimediaPhotos,
        flickrPhotos = artist.flickrPhotos,
        sources = song.sources,
        logEntries = song.logEntries
    )

    fun findByIdDetails(id: Long): AggregateSong {
        log.info("Getting song with id $id")
        val song = songRepository.findById(id).orElseThrow()
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()

        val photos = Flux.mergeSequential(artist.flickrPhotos.map { flickrApiPhoto ->
            flickrApiClient.getPhoto(flickrApiPhoto.flickrId)
        })
        val owners = Flux.mergeSequential(photos.map { flickrApiClient.getOwnerInformation(it.ownerId) })
            .sort { o1, o2 -> o1.username.compareTo(o2.username) }
        val licences2 = Flux.mergeSequential(flickrApiClient.getLicenses().map { Flux.fromIterable(it.license) })

        val photoDetails = Flux.mergeSequential(photos.map { photo ->
            val licenseMono = licences2.filter { it.id == photo.licenseId }.last()
            val ownerMono = owners.filter { it.id == photo.ownerId }.last()

            Mono.zip(licenseMono, ownerMono) { license, owner ->
                PhotoDetail(
                    id = photo.id,
                    url = photo.url,
                    farm = photo.farm,
                    server = photo.server,
                    secret = photo.secret,
                    title = photo.title,
                    licenseDetail = if (license == null) null else License(
                        id = license.id,
                        name = license.name,
                        url = license.url
                    ),
                    ownerDetail = if (owner == null) null else Owner(
                        id = owner.id,
                        username = owner.username,
                        photosUrl = owner.photosUrl
                    )

                )
            }
        })

        return AggregateSong(
            id = song.id ?: throw RuntimeException(),
            name = song.name,
            title = song.title,
            artistName = artist.name,
            artistImage = song.artistImage,
            background = song.background,
            youtube = song.youtube,
            spotify = song.spotify,
            status = song.status,
            sources = song.sources,
            wikimediaPhotos = artist.wikimediaPhotos,
            flickrPhotos = artist.flickrPhotos,
            flickrPhotoDetail = photoDetails,
            logEntries = song.logEntries
        )
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
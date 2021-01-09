package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
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
    val mongoSongRepository: MongoSongRepository,
    val songRepository: SongRepository,
    val flickrApiClient: FlickrApiClient
) {
    private val log = LoggerFactory.getLogger(SongService::class.java)

    fun countSongs(): Long {
        return songRepository.count()
    }

    fun findById(id: Long): Pair<Song, Set<PhotoDetail>> {
        log.info("Getting song with id $id")
        val song = songRepository.findById(id).orElseThrow()
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()

        val photos = artist.flickrPhotos.map { flickrApiPhoto ->
            flickrApiClient.getPhoto(flickrApiPhoto.flickrId).blockOptional().orElseThrow()
        }
        val licenses = flickrApiClient.getLicenses().block()!!
        val owners = photos.map { flickrApiClient.getOwnerInformation(it.ownerId).blockOptional().orElseThrow() }
            .sortedBy { it.username }

        val photoDetails = photos.map { photo ->
            val license = licenses.license.first { it.id == photo.licenseId }
            val owner = owners.first { it.id == photo.ownerId }
            convertToPhotoDetail(photo, license, owner)
        }.toSet()

        return Pair(
            first = Song(
                id = song.id,
                name = song.name,
                title = song.title,
                background = song.background,
                youtube = song.youtube,
                spotify = song.spotify,
                artistImage = song.artistImage,
                status = song.status,
                sources = song.sources.map {
                    SongSource(
                        url = it.url,
                        name = it.name
                    )
                }.toMutableList(),
                artists = song.artists
            ),
            second = photoDetails
        )
    }

    private fun convertToPhotoDetail(
        photo: FlickrPhotoDetail,
        license: FlickrApiLicense?,
        owner: FlickrApiOwner?
    ): PhotoDetail =
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
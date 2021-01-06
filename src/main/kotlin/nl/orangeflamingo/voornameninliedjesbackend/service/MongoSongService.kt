package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.repository.mongo.MongoSongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MongoSongService {

    private val log = LoggerFactory.getLogger(MongoSongService::class.java)

    @Autowired
    private lateinit var mongoSongRepository: MongoSongRepository

    @Autowired
    private lateinit var flickrApiClient: FlickrApiClient

    fun findAllArtistNames(): List<String> {
        return mongoSongRepository.findAll()
            .map { it.artist }
            .distinct()
    }

    fun findAllByStatusOrderByName(songStatus: SongStatus): List<MongoSong> {
        log.info("Getting all songs with status ${songStatus.name}")
        return mongoSongRepository.findAllByStatusOrderByName(SongStatus.SHOW).map { song ->
            MongoSong(
                id = song.id,
                name = song.name,
                title = song.title,
                artist = song.artist,
                background = song.background,
                youtube = song.youtube,
                spotify = song.spotify,
                artistImage = song.artistImage,
                status = song.status,
                wikimediaPhotos = emptySet(),
                flickrPhotos = emptySet(),
                sources = emptySet()
            )
        }
    }

    fun findById(id: String): MongoSong {
        log.info("Getting song with id $id")
        val song: DbSong = mongoSongRepository.findById(id).orElseThrow()

        val photos = song.flickrPhotos.map { flickrApiPhoto ->
            flickrApiClient.getPhoto(flickrApiPhoto).blockOptional().orElseThrow()
        }
        val licenses = flickrApiClient.getLicenses().block()!!
        val owners = photos.map { flickrApiClient.getOwnerInformation(it.ownerId).blockOptional().orElseThrow() }
            .sortedBy { it.username }

        val photoDetail = photos.map { photo ->
            val license = licenses.license.first { it.id == photo.licenseId }
            val owner = owners.first { it.id == photo.ownerId }
            convertToPhotoDetail(photo, license, owner)
        }

        return MongoSong(
            id = song.id,
            name = song.name,
            title = song.title,
            artist = song.artist,
            background = song.background,
            youtube = song.youtube,
            spotify = song.spotify,
            artistImage = song.artistImage,
            status = song.status,
            wikimediaPhotos = song.wikimediaPhotos,
            flickrPhotos = photoDetail.toSet(),
            sources = song.sources.map {
                SourceDetail(
                    url = it.url,
                    name = it.name
                )
            }.toSet()
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
}
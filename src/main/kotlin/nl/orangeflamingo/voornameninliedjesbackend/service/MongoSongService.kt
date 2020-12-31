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
        val song = mongoSongRepository.findById(id).orElseThrow()

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
            flickrPhotos = emptySet()
        )

//        val photos = song.flatMap { s ->
//            Flux.fromIterable(s.flickrPhotos)
//                .parallel()
//                .flatMap { flickrApiPhoto ->
//                    flickrApiClient.getPhoto(flickrApiPhoto)
//                }
//                .collectSortedList { o1, o2 -> o1.id.compareTo(o2.id) }
//        }
//
//        val licenses = flickrApiClient.getLicenses()
//
//        val owners = photos.flatMap { p ->
//            Flux.fromIterable(p)
//                .parallel()
//                .flatMap { photoDetail ->
//                    flickrApiClient.getOwnerInformation(photoDetail.ownerId)
//                }
//                .collectSortedList { o1, o2 -> o1.username.compareTo(o2.username) }
//        }
//
//        val photosDetail = Mono.zip(photos, licenses, owners).map {
//            val p = it.t1
//            val l = it.t2
//            val o = it.t3
//
//            p.map { photo ->
//                val license = l.license.first { license -> license.id == photo.licenseId }
//                val owner = o.first { owner -> owner.id == photo.ownerId }
//
//                convertToPhotoDetail(photo, license, owner)
//            }
//        }
//
//        return Mono.zip(song, photosDetail) { s, flickrPhotos ->
//            MongoSong(
//                id = s.id,
//                name = s.name,
//                title = s.title,
//                artist = s.artist,
//                background = s.background,
//                youtube = s.youtube,
//                spotify = s.spotify,
//                artistImage = s.artistImage,
//                status = s.status,
//                wikimediaPhotos = s.wikimediaPhotos,
//                flickrPhotos = flickrPhotos.map { it }.toSet(),
//                sources = s.sources.map {
//                    SourceDetail(
//                        url = it.url,
//                        name = it.name
//                    )
//                }.toSet()
//            )
//        }
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
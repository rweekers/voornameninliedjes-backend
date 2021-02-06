package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.client.WikipediaApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.*
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
    val songRepository: SongRepository,
    val flickrApiClient: FlickrApiClient,
    val wikipediaApiClient: WikipediaApiClient
) {
    private val log = LoggerFactory.getLogger(SongService::class.java)

    fun countSongs(): Long {
        return songRepository.count()
    }

    fun findAll(): List<AggregateSong> {
        return songRepository.findAllOrderByNameAsc()
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()
                createAggregateSong(song, artist)
            }
    }

    fun findByName(name: String): List<AggregateSong> {
        return songRepository.findAllByNameIgnoreCaseOrderByNameAsc(name)
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()
                createAggregateSong(song, artist)
            }
    }

    fun findByNameStartsWith(firstCharacter: String): List<AggregateSong> {
        return songRepository.findAllByNameStartingWithIgnoreCaseOrderByNameAsc(firstCharacter)
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
        artist: Artist,
        photoDetails: Flux<PhotoDetail> = Flux.empty(),
        wikipediaBackground: Mono<String> = Mono.empty()
    ) = AggregateSong(
        id = song.id ?: throw IllegalStateException("The song should have an id"),
        title = song.title,
        name = song.name,
        artistName = artist.name,
        background = song.background,
        wikipediaBackground = wikipediaBackground.switchIfEmpty(Mono.empty()),
        youtube = song.youtube,
        spotify = song.spotify,
        status = song.status,
        artistImage = song.artistImage,
        wikimediaPhotos = artist.wikimediaPhotos,
        flickrPhotos = artist.flickrPhotos,
        flickrPhotoDetail = photoDetails,
        sources = song.sources,
        logEntries = song.logEntries
    )

    fun findByIdDetails(id: Long): AggregateSong {
        log.info("Getting song with id $id")

        val song = songRepository.findById(id).orElseThrow()
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist).orElseThrow()
        val wikipediaBackground =
            if (song.wikipediaPage != null) wikipediaApiClient.getBackground(song.wikipediaPage) else Mono.empty()

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

        return createAggregateSong(
            song, artist, photoDetails, wikipediaBackground.switchIfEmpty(Mono.empty()).map { it.background }
        )
    }

    fun updateSong(aggregateSong: AggregateSong, song: Song, user: String): AggregateSong {
        val artist =
            findLeadArtistForSong(song) ?: throw IllegalStateException("There should be a lead artist for all songs")
        song.title = aggregateSong.title
        song.name = aggregateSong.name
        song.status = aggregateSong.status
        song.background = aggregateSong.background
        song.youtube = aggregateSong.youtube
        song.spotify = aggregateSong.spotify
        song.sources = aggregateSong.sources.map { s -> SongSource(url = s.url, name = s.name) }
        song.logEntries.add(SongLogEntry(Instant.now(), user))
        val savedSong = songRepository.save(song)

        // update artist
        if (artistUpdate(aggregateSong, artist)) {
            artist.wikimediaPhotos =
                aggregateSong.wikimediaPhotos.map { ArtistWikimediaPhoto(it.url, it.attribution) }.toMutableSet()
            artist.flickrPhotos = aggregateSong.flickrPhotos.map { ArtistFlickrPhoto(it.flickrId) }.toMutableSet()
            artist.name = aggregateSong.artistName
            val artistLogEntry = ArtistLogEntry(Instant.now(), user)
            artist.logEntries.add(artistLogEntry)
            artistRepository.save(artist)
        }

        return createAggregateSong(savedSong, artist)
    }

    fun findLeadArtistForSong(song: Song): Artist? {
        val artist = song.artists
            .filter { artistRef -> artistRef.originalArtist }
            .map { artistRepository.findById(it.artist) }
            .first().orElseThrow()
        return artist
    }

    private fun artistUpdate(song: AggregateSong, artist: Artist): Boolean {
        if (song.artistName != artist.name) return true

        if (song.flickrPhotos != artist.flickrPhotos.map { it.flickrId }) return true

        if (song.wikimediaPhotos != artist.wikimediaPhotos) return true

        return false
    }

    fun newSong(aggregateSong: AggregateSong, user: String): AggregateSong {
        val artist = artistRepository.findFirstByName(aggregateSong.artistName) ?: artistRepository.save(
            Artist(
                name = aggregateSong.artistName,
                background = aggregateSong.artistBackground,
                wikimediaPhotos = aggregateSong.wikimediaPhotos.toMutableSet(),
                flickrPhotos = aggregateSong.flickrPhotos.toMutableSet(),
                logEntries = mutableListOf(
                    ArtistLogEntry(
                        date = Instant.now(),
                        username = user
                    )
                )
            )
        )

        val song = Song(
            title = aggregateSong.title,
            name = aggregateSong.name,
            artistImage = aggregateSong.artistImage,
            background = aggregateSong.background,
            youtube = aggregateSong.youtube,
            spotify = aggregateSong.spotify,
            status = aggregateSong.status,
            sources = aggregateSong.sources.map {
                SongSource(
                    url = it.url,
                    name = it.name
                )
            }.toMutableList(),
            logEntries = aggregateSong.logEntries.map {
                SongLogEntry(
                    date = it.date,
                    username = it.username
                )
            }.toMutableList()
        )
        song.addArtist(artist)
        val songDb = songRepository.save(song)
        return createAggregateSong(songDb, artist)
    }
}
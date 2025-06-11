package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.AggregateSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.License
import nl.orangeflamingo.voornameninliedjesbackend.domain.Owner
import nl.orangeflamingo.voornameninliedjesbackend.domain.PhotoDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongSource
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatusStatistics
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongPhoto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.Locale

@Service
class SongService @Autowired constructor(
    val artistRepository: ArtistRepository,
    val songRepository: SongRepository,
    val flickrApiClient: FlickrApiClient
) {
    private val log = LoggerFactory.getLogger(SongService::class.java)

    fun countSongs(): Long {
        return songRepository.count()
    }

    fun countSongsForStatus(status: SongStatus): Long =
        songRepository.countAllByStatusIs(status)

    fun countSongsByStatus(): List<SongStatusStatistics> =
        songRepository.getCountPerStatus()

    fun findAll(): List<AggregateSong> {
        return mapSongs(songRepository.findAllOrderByNameAscTitleAsc())
    }

    fun findByName(name: String): List<AggregateSong> {
        return mapSongs(songRepository.findAllByNameIgnoreCaseOrderByNameAscTitleAsc(name))
    }

    fun findByNameStartsWithAndStatusIn(firstCharacter: String, statusList: List<SongStatus>): List<AggregateSong> {
        return mapSongs(songRepository.findAllByNameStartingWithIgnoreCaseAndStatusInOrderByNameAscTitleAsc(firstCharacter, statusList))
    }

    fun findAllByStatusOrderedByName(status: SongStatus): List<AggregateSong> {
        log.info("Getting all songs by status ordered by name...")
        return mapSongs(songRepository.findAllByStatusOrderedByNameAndTitle(status.code))
    }

    fun findAllByStatusOrderedByNameFilteredByFirstCharacter(statuses: List<SongStatus>, firstChars: List<Char>): List<AggregateSong> {
        log.info(
            "Getting all songs for statuses {} and first characters {}...",
            statuses.joinToString { it.code },
            firstChars.joinToString()
        )
        return mapSongs(songRepository.findAllByStatusesAndNameStartingWithOrderedByNameAndTitle(
            statuses.map { it.code },
            firstChars.map {
                it.lowercase(
                    Locale.getDefault()
                )
            }))
    }

    private fun mapSongs(songs: List<Song>): List<AggregateSong> {
        return songs
            .map { song ->
                val artist = artistRepository.findById(song.artist.id?:throw IllegalStateException())
                    .orElseThrow { ArtistNotFoundException("Artist with artist id ${song.artist.id} for title ${song.title} not found") }
                createAggregateSong(song, artist)
            }
    }

    fun findById(id: Long): AggregateSong {
        log.info("Getting song with id $id")
        val song = songRepository.findById(id).orElseThrow { SongNotFoundException("Song with id $id not found") }
        val artist = artistRepository.findById(song.artist.id?:throw IllegalStateException())
            .orElseThrow { ArtistNotFoundException("Artist with artist id ${song.artist.id} for title ${song.title} not found") }

        return createAggregateSong(song, artist)
    }

    private fun createAggregateSong(
        song: Song,
        artist: Artist,
        photoDetails: Flux<PhotoDetail> = Flux.empty()
    ) = AggregateSong(
        id = song.id ?: throw IllegalStateException("The song should have an id"),
        title = song.title,
        name = song.name,
        artistName = artist.name,
        artistMbid = artist.mbid.toString(),
        artistLastFmUrl = artist.lastFmUrl.toString(),
        background = song.background,
        wikipediaPage = song.wikipediaPage,
        youtube = song.youtube,
        spotify = song.spotify,
        wikipediaContentNl = song.wikiContentNl,
        wikipediaContentEn = song.wikiContentEn,
        wikipediaSummaryEn = song.wikiSummaryEn,
        mbid = song.mbid,
        lastFmUrl = song.lastFmUrl,
        albumName = song.albumName,
        albumMbid = song.albumMbid,
        albumLastFmUrl = song.albumLastFmUrl,
        status = song.status,
        remarks = song.remarks,
        hasDetails = song.hasDetails,
        artistImage = song.artistImage,
        artistImageAttribution = song.artistImageAttribution,
        localImage = song.localImage,
        blurredImage = song.blurredImage,
        artistImageWidth = song.artistImageWidth,
        artistImageHeight = song.artistImageHeight,
        artistPhotos = artist.photos,
        songPhotos = song.photos,
        flickrPhotos = artist.flickrPhotos,
        flickrPhotoDetail = photoDetails,
        sources = song.sources,
        tags = song.lastFmTags.toList(),
        logEntries = song.logEntries
    )

    fun findByArtistAndNameDetails(artist: String, title: String): AggregateSong {
        return songRepository.findByArtistAndTitle(artist, title).map { getDetails(it) }
            .orElseThrow { SongNotFoundException("Song with title $title and artist $artist not found") }
    }

    private fun getDetails(song: Song): AggregateSong {
        log.info("Getting song with id ${song.id}")

        val artist = artistRepository.findById(song.artist.id?:throw IllegalArgumentException())
            .orElseThrow { ArtistNotFoundException("Artist with artist id ${song.artist.id} for title ${song.title} not found") }

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
                    licenseDetail = License(
                        id = license.id,
                        name = license.name,
                        url = license.url
                    ),
                    ownerDetail = Owner(
                        id = owner.id,
                        username = owner.username,
                        photosUrl = owner.photosUrl
                    )
                )
            }
        })

        return createAggregateSong(
            song, artist, photoDetails
        )
    }

    fun updateSong(aggregateSong: AggregateSong, song: Song, user: String): AggregateSong {
        val artist =
            findArtistForSong(song)
        song.title = aggregateSong.title
        song.name = aggregateSong.name
        song.status = aggregateSong.status
        song.remarks = aggregateSong.remarks
        song.hasDetails = aggregateSong.hasDetails
        song.background = aggregateSong.background
        song.wikipediaPage = aggregateSong.wikipediaPage
        song.youtube = aggregateSong.youtube
        song.spotify = aggregateSong.spotify
        song.photos = aggregateSong.songPhotos.map { SongPhoto(url = it.url, attribution = it.attribution ) }.toMutableSet()
        song.sources = aggregateSong.sources.map { SongSource(url = it.url, name = it.name) }.toSet()
        song.logEntries.add(SongLogEntry(date = Instant.now(), username = user))
        val savedSong = songRepository.save(song)

        // update artist
        if (artistUpdate(aggregateSong, artist)) {
            artist.photos =
                aggregateSong.artistPhotos.map { ArtistPhoto(url = it.url, attribution = it.attribution) }.toMutableSet()
            artist.flickrPhotos = aggregateSong.flickrPhotos.map { ArtistFlickrPhoto(flickrId = it.flickrId) }.toMutableSet()
            artist.name = aggregateSong.artistName
            val artistLogEntry = ArtistLogEntry(date = Instant.now(), username = user)
            artist.logEntries.add(artistLogEntry)
            artistRepository.save(artist)
        }

        return createAggregateSong(savedSong, artist)
    }

    fun findArtistForSong(song: Song): Artist {
        return artistRepository.findById(song.artist.id ?: throw IllegalStateException())
            .orElseThrow { ArtistNotFoundException("Artist with artist id ${song.artist.id} for title ${song.title} not found") }
    }

    private fun artistUpdate(song: AggregateSong, artist: Artist): Boolean {
        if (song.artistName != artist.name) return true

        if (song.flickrPhotos != artist.flickrPhotos.map { it.flickrId }) return true

        return song.artistPhotos != artist.photos
    }

    fun newSong(aggregateSong: AggregateSong, user: String): AggregateSong {
        val artist = artistRepository.findFirstByName(aggregateSong.artistName) ?: artistRepository.save(
            Artist(
                name = aggregateSong.artistName,
                background = aggregateSong.artistBackground,
                photos = aggregateSong.artistPhotos.toMutableSet(),
                flickrPhotos = aggregateSong.flickrPhotos.toMutableSet(),
                logEntries = mutableSetOf(
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
            artistImageAttribution = aggregateSong.artistImageAttribution,
            background = aggregateSong.background,
            wikipediaPage = aggregateSong.wikipediaPage,
            youtube = aggregateSong.youtube,
            spotify = aggregateSong.spotify,
            photos = aggregateSong.songPhotos.map { SongPhoto(url = it.url, attribution = it.attribution ) }.toMutableSet(),
            status = aggregateSong.status,
            remarks = aggregateSong.remarks,
            hasDetails = aggregateSong.hasDetails,
            sources = aggregateSong.sources.map {
                SongSource(
                    url = it.url,
                    name = it.name
                )
            }.toMutableSet(),
            logEntries = aggregateSong.logEntries.map {
                SongLogEntry(
                    date = it.date,
                    username = it.username
                )
            }.toMutableSet(),
            artist = AggregateReference.to(artist.id ?: throw IllegalStateException())
        )
        val songDb = songRepository.save(song)
        return createAggregateSong(songDb, artist)
    }
}
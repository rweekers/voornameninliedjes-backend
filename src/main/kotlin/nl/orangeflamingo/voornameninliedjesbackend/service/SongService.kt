package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.AggregateSong
import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistFlickrPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.License
import nl.orangeflamingo.voornameninliedjesbackend.domain.Owner
import nl.orangeflamingo.voornameninliedjesbackend.domain.PhotoDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongLogEntry
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongSource
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongWikimediaPhoto
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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

    fun findAll(): List<AggregateSong> {
        return songRepository.findAllOrderByNameAscTitleAsc()
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
                    .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }
                createAggregateSong(song, artist)
            }
    }

    fun findByName(name: String): List<AggregateSong> {
        return songRepository.findAllByNameIgnoreCaseOrderByNameAscTitleAsc(name)
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
                    .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }
                createAggregateSong(song, artist)
            }
    }

    fun findByNameStartsWithAndStatusIn(firstCharacter: String, statusList: List<SongStatus>): List<AggregateSong> {
        return songRepository.findAllByNameStartingWithIgnoreCaseAndStatusInOrderByNameAscTitleAsc(firstCharacter, statusList.map { it.code })
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
                    .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }
                createAggregateSong(song, artist)
            }
    }

    fun findAllByStatusOrderedByName(status: SongStatus): List<AggregateSong> {
        log.info("Getting all songs by status ordered by name...")
        return songRepository.findAllByStatusOrderedByNameAndTitle(status.code)
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
                    .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }
                createAggregateSong(song, artist)
            }
    }

    fun findAllByStatusOrderedByNameFilteredByFirstCharacter(statuses: List<SongStatus>, firstChars: List<Char>): List<AggregateSong> {
        log.info(
            "Getting all songs for statuses {} and first characters {}...",
            statuses.joinToString { it.code },
            firstChars.joinToString()
        )
        return songRepository.findAllByStatusesAndNameStartingWithOrderedByNameAndTitle(
            statuses.map { it.code },
            firstChars.map {
                it.lowercase(
                    Locale.getDefault()
                )
            })
            .map { song ->
                val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
                    .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }
                createAggregateSong(song, artist)
            }
    }

    fun findById(id: Long): AggregateSong {
        log.info("Getting song with id $id")
        val song = songRepository.findById(id).orElseThrow { SongNotFoundException("Song with id $id not found") }
        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
            .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }

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
        artistMbid = artist.mbid,
        artistLastFmUrl = artist.lastFmUrl,
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
        artistImageWidth = song.artistImageWidth,
        artistImageHeight = song.artistImageHeight,
        artistWikimediaPhotos = artist.wikimediaPhotos,
        songWikimediaPhotos = song.wikimediaPhotos,
        flickrPhotos = artist.flickrPhotos,
        flickrPhotoDetail = photoDetails,
        sources = song.sources,
        tags = song.lastFmTags.toSet(),
        logEntries = song.logEntries
    )

    fun findByArtistAndNameDetails(artist: String, title: String): AggregateSong {
        return songRepository.findByArtistAndTitle(artist, title).map { getDetails(it) }
            .orElseThrow { SongNotFoundException("Song with title $title and artist $artist not found") }
    }

    private fun getDetails(song: Song): AggregateSong {
        log.info("Getting song with id ${song.id}")

        val artist = artistRepository.findById(song.artists.first { it.originalArtist }.artist)
            .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }

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
            song, artist, photoDetails
        )
    }

    fun updateSong(aggregateSong: AggregateSong, song: Song, user: String): AggregateSong {
        val artist =
            findLeadArtistForSong(song)
        song.title = aggregateSong.title
        song.name = aggregateSong.name
        song.status = aggregateSong.status
        song.remarks = aggregateSong.remarks
        song.hasDetails = aggregateSong.hasDetails
        song.background = aggregateSong.background
        song.wikipediaPage = aggregateSong.wikipediaPage
        song.youtube = aggregateSong.youtube
        song.spotify = aggregateSong.spotify
        song.wikimediaPhotos = aggregateSong.songWikimediaPhotos.map { SongWikimediaPhoto(url = it.url, attribution = it.attribution ) }.toMutableSet()
        song.sources = aggregateSong.sources.map { SongSource(url = it.url, name = it.name) }
        song.logEntries.add(SongLogEntry(Instant.now(), user))
        val savedSong = songRepository.save(song)

        // update artist
        if (artistUpdate(aggregateSong, artist)) {
            artist.wikimediaPhotos =
                aggregateSong.artistWikimediaPhotos.map { ArtistWikimediaPhoto(it.url, it.attribution) }.toMutableSet()
            artist.flickrPhotos = aggregateSong.flickrPhotos.map { ArtistFlickrPhoto(it.flickrId) }.toMutableSet()
            artist.name = aggregateSong.artistName
            val artistLogEntry = ArtistLogEntry(Instant.now(), user)
            artist.logEntries.add(artistLogEntry)
            artistRepository.save(artist)
        }

        return createAggregateSong(savedSong, artist)
    }

    fun findLeadArtistForSong(song: Song): Artist {
        return song.artists
            .filter { artistRef -> artistRef.originalArtist }
            .map { artistRepository.findById(it.artist) }
            .first()
            .orElseThrow { ArtistNotFoundException("Artist with artistRef ${song.artists.first { it.originalArtist }} for title ${song.title} not found") }
    }

    private fun artistUpdate(song: AggregateSong, artist: Artist): Boolean {
        if (song.artistName != artist.name) return true

        if (song.flickrPhotos != artist.flickrPhotos.map { it.flickrId }) return true

        if (song.artistWikimediaPhotos != artist.wikimediaPhotos) return true

        return false
    }

    fun newSong(aggregateSong: AggregateSong, user: String): AggregateSong {
        val artist = artistRepository.findFirstByName(aggregateSong.artistName) ?: artistRepository.save(
            Artist(
                name = aggregateSong.artistName,
                background = aggregateSong.artistBackground,
                wikimediaPhotos = aggregateSong.artistWikimediaPhotos.toMutableSet(),
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
            artistImageAttribution = aggregateSong.artistImageAttribution,
            background = aggregateSong.background,
            wikipediaPage = aggregateSong.wikipediaPage,
            youtube = aggregateSong.youtube,
            spotify = aggregateSong.spotify,
            wikimediaPhotos = aggregateSong.songWikimediaPhotos.map { SongWikimediaPhoto(url = it.url, attribution = it.attribution ) }.toMutableSet(),
            status = aggregateSong.status,
            remarks = aggregateSong.remarks,
            hasDetails = aggregateSong.hasDetails,
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
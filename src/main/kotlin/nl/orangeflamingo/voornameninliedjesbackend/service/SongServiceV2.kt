package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.Artist
import nl.orangeflamingo.voornameninliedjesbackend.domain.ArtistPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmAlbum
import nl.orangeflamingo.voornameninliedjesbackend.domain.PaginatedSongs
import nl.orangeflamingo.voornameninliedjesbackend.domain.Photo
import nl.orangeflamingo.voornameninliedjesbackend.domain.Song
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongDetail
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongPhoto
import nl.orangeflamingo.voornameninliedjesbackend.domain.SongStatus
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.ArtistRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongDetailRepository
import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.SongRepositoryV2
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SongServiceV2(
    private val songRepositoryV2: SongRepositoryV2,
    private val songDetailRepository: SongDetailRepository,
    private val artistRepository: ArtistRepository
) {
    @Cacheable(
        "songsByPrefix",
        key = "(#firstChars?.toLowerCase() ?: '') + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()"
    )
    fun findByNameStartingWith(firstChars: String?, status: SongStatus, pageable: Pageable): PaginatedSongs {
        val totalCount = songRepositoryV2.countAllSongsWithArtistsStartingWith(status.code, firstChars)

        return PaginatedSongs(
            songRepositoryV2
                .findAllSongsWithArtistsStartingWith(status.code, firstChars, pageable.pageSize, pageable.pageNumber),
            totalCount,
            pageable.pageNumber + pageable.pageSize >= totalCount
        )
    }

    fun findByArtistAndTitle(artist: String, title: String): SongDetail {
        val song = songDetailRepository.findByArtistAndTitle(artist, title).orElseThrow { SongNotFoundException("Song with title $title and artist $artist not found") }

        val artistDb = artistRepository.findById(song.artist.id ?: throw IllegalArgumentException())
            .orElseThrow { IllegalArgumentException() }

        return convert(song, artistDb)
    }

    private fun convert(song: Song, artist: Artist): SongDetail {
        val (name, url) = song.albumName to song.albumLastFmUrl

        return SongDetail(
            artist = artist.name,
            title = song.title,
            name = song.name,
            hasDetails = song.hasDetails,
            youtube = song.youtube,
            spotify = song.spotify,
            background = song.background,
            artistImageWidth = song.artistImageWidth,
            artistImageHeight = song.artistImageHeight,
            wikipediaPage = song.wikipediaPage,
            wikiContentNl = song.wikiContentNl,
            wikiContentEn = song.wikiContentEn,
            wikiSummaryEn = song.wikiSummaryEn,
            blurredImage = song.blurredImage,
            localImage = song.localImage,
            artistImage = song.artistImage,
            artistImageAttribution = song.artistImageAttribution,
            lastfmAlbum = if (name != null && url != null) LastFmAlbum(name, song.albumMbid, url) else null,
            photos = song.photos.map { convert(it) } + artist.photos.map { convert(it) },
            sources = song.sources.toList(),
            tags = song.lastFmTags.toList()
        )
    }

    private fun convert(wikimediaPhoto: SongPhoto): Photo {
        return Photo(url = wikimediaPhoto.url, attribution = wikimediaPhoto.attribution)
    }

    private fun convert(wikimediaPhoto: ArtistPhoto): Photo {
        return Photo(url = wikimediaPhoto.url.toString(), attribution = wikimediaPhoto.attribution)
    }

}
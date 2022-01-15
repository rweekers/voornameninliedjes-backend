package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmAlbum
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmArtist
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmError
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmResponse
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmResponseDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrack
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmWiki
import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@Profile("!integration-test")
class LastFmHttpApiClient(
    @Autowired val lastFmWebClient: WebClient
) : LastFmApiClient {

    private val lastFmKey = "fbfbf9c47ff8bcf4642ece8c7de2305a"

    override fun getTrack(artist: String, title: String): Mono<LastFmResponse> {
        return lastFmWebClient.get().uri(
            "?method=track.getInfo&api_key=$lastFmKey&artist={artist}&track={title}&format=json",
            artist,
            title
        )
            .retrieve()
            .bodyToMono(
                LastFmResponseDto::class.java
            )
            .switchIfEmpty(Mono.error { throw LastFmException("Gotten empty response from last.fm!") })
            .map {
                when (it.error) {
                    null -> {
                        val track = it.track ?: throw LastFmException("Track not found on last.fm response!")
                        LastFmTrack(
                            name = track.name,
                            mbid = track.mbid,
                            url = track.url,
                            artist = LastFmArtist(
                                name = track.artist.name,
                                mbid = track.artist.mbid,
                                url = track.artist.url
                            ),
                            album = if (track.album != null) LastFmAlbum(
                                name = track.album.title,
                                mbid = track.album.mbid,
                                url = track.album.url
                            ) else null,
                            tags = track.toptags.tag.map { tag ->
                                LastFmTag(
                                    name = tag.name,
                                    url = tag.url
                                )
                            },
                            wiki = if (track.wiki != null) LastFmWiki(
                                summary = track.wiki.summary,
                                content = track.wiki.content
                            ) else null
                        )
                    }
                    else -> LastFmError(it.error, it.message)
                }
            }
    }
}
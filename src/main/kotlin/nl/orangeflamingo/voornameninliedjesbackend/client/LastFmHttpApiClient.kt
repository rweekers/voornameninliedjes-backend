package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmAlbum
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmArtist
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmResponseDto
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrack
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmWiki
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

    override fun getTrack(artist: String, title: String): Mono<LastFmTrack> {
        return lastFmWebClient.get().uri(
            "?method=track.getInfo&api_key=$lastFmKey&artist={artist}&track={title}&format=json",
            artist,
            title
        )
            .retrieve()
            .bodyToMono(
                LastFmResponseDto::class.java
            )
            .onErrorResume { Mono.empty() }
            .switchIfEmpty(Mono.empty())
            .map {
                LastFmTrack(
                    name = it.track.name,
                    mbid = it.track.mbid,
                    url = it.track.url,
                    artist = LastFmArtist(
                        name = it.track.artist.name,
                        mbid = it.track.artist.mbid,
                        url = it.track.artist.url
                    ),
                    album = if (it.track.album != null) LastFmAlbum(
                        name = it.track.album.title,
                        mbid = it.track.album.mbid,
                        url = it.track.album.url
                    ) else null,
                    tags = it.track.toptags.tag.map { tag ->
                        LastFmTag(
                            name = tag.name,
                            url = tag.url
                        )
                    },
                    wiki = if (it.track.wiki != null) LastFmWiki(
                        summary = it.track.wiki.summary,
                        content = it.track.wiki.content
                    ) else null
                )
            }
    }
}
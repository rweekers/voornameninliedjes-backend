package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.*
import nl.orangeflamingo.voornameninliedjesbackend.service.LastFmException
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class LastFmHttpApiClient(
    @Autowired val lastFmWebClient: WebClient,
    private val lastFmKey: String
) : LastFmApiClient {

    @RegisterReflectionForBinding(LastFmResponseDto::class)
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
            .handle { lastFmResponse, sink ->
                when (lastFmResponse.error) {
                    null -> {
                        if (lastFmResponse.track == null) sink.error(LastFmException("Track not found on last.fm response!"))
                        val track = lastFmResponse.track!!
                        sink.next(LastFmTrack(
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
                        ))
                    }
                    else -> sink.next(LastFmError(lastFmResponse.error, lastFmResponse.message))
                }
            }
    }
}
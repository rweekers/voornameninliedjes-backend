package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmAlbum
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmArtist
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTag
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmTrack
import nl.orangeflamingo.voornameninliedjesbackend.domain.LastFmWiki
import reactor.core.publisher.Mono

class FakeLastFmApiClient : LastFmApiClient {
    override fun getTrack(artist: String, title: String): Mono<LastFmTrack> {
        return Mono.just(
            LastFmTrack(
                name = "fake track name",
                mbid = "fake track mbid",
                url = "fake track url",
                album = LastFmAlbum(
                    name = "fake album name",
                    mbid = "fake album mbid",
                    url = "fake album url"
                ),
                artist = LastFmArtist(
                    name = "fake artist name",
                    mbid = "fake artist mbid",
                    url = "fake artist url"
                ),
                tags = listOf(
                    LastFmTag(
                        name = "fake tag name #1",
                        url = "fake tag url #1"
                    ),
                    LastFmTag(
                        name = "fake tag name #2",
                        url = "fake tag url #2"
                    )
                ),
                wiki = LastFmWiki(
                    summary = "Fake wiki summary",
                    content = "Fake wiki content"
                )
            )
        )
    }
}
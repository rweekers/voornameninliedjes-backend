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
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestClient
import java.util.Optional

class LastFmHttpApiClient(
    @param:Autowired val lastFmRestClient: RestClient,
    private val lastFmKey: String
) : LastFmApiClient {

    @RegisterReflectionForBinding(LastFmResponseDto::class)
    override fun getTrack(artist: String, title: String): Optional<LastFmResponse> {
        return Optional.ofNullable(
            lastFmRestClient.get().uri(
                "?method=track.getInfo&api_key=$lastFmKey&artist={artist}&track={title}&format=json",
                artist,
                title
            )
                .retrieve()
                .body(LastFmResponseDto::class.java)
        ).map { responseBody ->
            if (responseBody.error == null) {
                val track = responseBody.track
                    ?: throw LastFmException("Track not found in Last.fm response!")

                LastFmTrack(
                    name = track.name,
                    mbid = track.mbid,
                    url = track.url,
                    artist = LastFmArtist(
                        name = track.artist.name,
                        mbid = track.artist.mbid,
                        url = track.artist.url
                    ),
                    album = track.album?.let {
                        LastFmAlbum(
                            name = it.title,
                            mbid = it.mbid,
                            url = it.url
                        )
                    },
                    tags = track.toptags.tag.map {
                        LastFmTag(
                            name = it.name,
                            url = it.url
                        )
                    },
                    wiki = track.wiki?.let {
                        LastFmWiki(
                            summary = it.summary,
                            content = it.content
                        )
                    }
                )
            } else {
                LastFmError(
                    responseBody.error,
                    responseBody.message
                )
            }
        }
    }
}
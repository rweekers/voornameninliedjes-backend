package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiLicense
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiLicenses
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiOwner
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrPhotoDetail
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiLicenseDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiOwnerDto
import nl.orangeflamingo.voornameninliedjesbackend.dto.FlickrApiPhotoDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@Profile("!integration-test")
class FlickrHttpApiClient(
    @Autowired val flickrWebClient: WebClient
) : FlickrApiClient {
    override fun getPhoto(flickrPhotoId: String): Mono<FlickrPhotoDetail> =
        flickrWebClient.get().uri(
            "/services/rest/?method=flickr.photos.getInfo&api_key=9676a28e9cb321d2721e813055abb6dc&format=json&nojsoncallback=true&photo_id={flickr_photo_id}",
            flickrPhotoId
        )
            .retrieve()
            .bodyToMono(FlickrApiPhotoDto::class.java)
            .onErrorResume { Mono.empty() }
            .switchIfEmpty(Mono.empty())
            .map {
                FlickrPhotoDetail(
                    url = "https://farm${it.photo.farm}.staticflickr.com/${it.photo.server}/${it.photo.id}_${it.photo.secret}_c.jpg",
                    title = it.photo.title.content,
                    id = it.photo.id,
                    server = it.photo.server,
                    secret = it.photo.secret,
                    farm = it.photo.farm,
                    ownerId = it.photo.owner.nsid,
                    licenseId = it.photo.license
                )
            }

    override fun getOwnerInformation(flickrOwnerId: String): Mono<FlickrApiOwner> =
        flickrWebClient.get().uri(
            "/services/rest/?method=flickr.people.getInfo&api_key=9676a28e9cb321d2721e813055abb6dc&format=json&nojsoncallback=true&user_id={flickr_owner_id}",
            flickrOwnerId
        )
            .retrieve()
            .bodyToMono(FlickrApiOwnerDto::class.java)
            .onErrorResume { Mono.empty() }
            .switchIfEmpty(Mono.empty())
            .map {
                FlickrApiOwner(
                    id = it.person.id,
                    username = it.person.username.content,
                    photosUrl = it.person.photosurl.content
                )
            }

    override fun getLicenses(): Mono<FlickrApiLicenses> =
        flickrWebClient.get()
            .uri("/services/rest/?method=flickr.photos.licenses.getInfo&api_key=9676a28e9cb321d2721e813055abb6dc&format=json&nojsoncallback=true")
            .retrieve()
            .bodyToMono(FlickrApiLicenseDto::class.java)
            .onErrorResume { Mono.empty() }
            .switchIfEmpty(Mono.empty())
            .map {
                FlickrApiLicenses(
                    license = it.licenses.license.map { license ->
                        FlickrApiLicense(
                            id = license.id,
                            name = license.name,
                            url = license.url
                        )
                    }
                )
            }

}
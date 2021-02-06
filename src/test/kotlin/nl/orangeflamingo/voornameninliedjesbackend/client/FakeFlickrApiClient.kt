package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.client.FlickrApiClient
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiLicense
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiLicenses
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiOwner
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrPhotoDetail
import reactor.core.publisher.Mono

class FakeFlickrApiClient : FlickrApiClient {
    override fun getPhoto(flickrPhotoId: String): Mono<FlickrPhotoDetail> {
        return Mono.just(
            FlickrPhotoDetail(
                url = "https://somefakeflickrphotourl.doesnotexist",
                title = "Fake Flickr title",
                farm = "fake farm",
                server = "fake server",
                id = flickrPhotoId,
                secret = "fake secret",
                ownerId = "fakeOwnerId",
                licenseId = "fakeLicenseId"
            )
        )
    }

    override fun getOwnerInformation(flickrOwnerId: String): Mono<FlickrApiOwner> {
        return Mono.just(
            FlickrApiOwner(
                id = flickrOwnerId,
                username = "fake username",
                photosUrl = "https://somefakeflickruserurl.doesnotexist"
            )
        )
    }

    override fun getLicenses(): Mono<FlickrApiLicenses> {
        return Mono.just(
            FlickrApiLicenses(
                listOf(
                    FlickrApiLicense(
                        id = "fakeLicenseId",
                        name = "some fake license",
                        url = "https://somefakelicense.doesnotexist"
                    )
                )
            )
        )
    }
}
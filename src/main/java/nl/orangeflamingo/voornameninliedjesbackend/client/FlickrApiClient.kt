package nl.orangeflamingo.voornameninliedjesbackend.client

import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiLicenses
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrApiOwner
import nl.orangeflamingo.voornameninliedjesbackend.domain.FlickrPhotoDetail
import reactor.core.publisher.Mono

interface FlickrApiClient {

    fun getPhoto(flickrPhotoId: String): Mono<FlickrPhotoDetail>

    fun getOwnerInformation(flickrOwnerId: String): Mono<FlickrApiOwner>

    fun getLicenses(): Mono<FlickrApiLicenses>
}
package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FlickrApiPhotoDto(
        val photo: FlickrApiPhotoDetailDto
)

data class FlickrApiPhotoDetailDto(
        val farm: String,
        val server: String,
        val id: String,
        val secret: String,
        val license: String,
        val owner: FlickrApiOwnerIdDto,
        val title: FlickrApiPhotoTitleDto
)

data class FlickrApiPhotoTitleDto(
        @JsonProperty("_content") val content: String
)

data class FlickrApiOwnerIdDto(
        val nsid: String
)

data class FlickrApiOwnerDto(
        val person: FlickrApiPersonDto
)

data class FlickrApiPersonDto(
        val id: String,
        val username: FlickrApiUsernameDto,
        val photosurl: FlickrApiPhotosurlDto
)

data class FlickrApiUsernameDto(
        @JsonProperty("_content") val content: String
)

data class FlickrApiPhotosurlDto(
        @JsonProperty("_content")val content: String
)

data class FlickrApiLicenseDto(
        val licenses: FlickrApiLicensesDto
)

data class FlickrApiLicensesDto(
        val license: List<FlickrApiLicenseDetailDto>
)

data class FlickrApiLicenseDetailDto(
        val id: String,
        val name: String,
        val url: String
)
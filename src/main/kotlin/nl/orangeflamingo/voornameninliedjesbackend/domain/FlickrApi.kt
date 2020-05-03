package nl.orangeflamingo.voornameninliedjesbackend.domain


data class FlickrPhotoDetail(
        val url: String,
        val title: String,
        val farm: String,
        val server: String,
        val id: String,
        val secret: String,
        val ownerId: String,
        val licenseId: String
)

data class FlickrApiOwner(
        val id: String,
        val username: String,
        val photosUrl: String
)

data class FlickrApiLicenses(
        val license: List<FlickrApiLicense>
)

data class FlickrApiLicense(
        val id: String,
        val name: String,
        val url: String
)
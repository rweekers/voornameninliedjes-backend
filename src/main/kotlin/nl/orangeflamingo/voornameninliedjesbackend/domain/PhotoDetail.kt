package nl.orangeflamingo.voornameninliedjesbackend.domain

data class PhotoDetail(
    val url: String,
    val farm: String,
    val server: String,
    val id: String,
    val title: String,
    val secret: String,
    val licenseDetail: License?,
    val ownerDetail: Owner?
)

data class License(
    val id: String,
    val name: String,
    val url: String
)

data class Owner(
    val id: String,
    val username: String,
    val photosUrl: String
)
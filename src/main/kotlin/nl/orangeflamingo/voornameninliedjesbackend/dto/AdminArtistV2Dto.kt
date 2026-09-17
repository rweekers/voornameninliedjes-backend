package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URI
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_ABSENT)
data class AdminArtistV2Dto @JsonCreator constructor(
    @param:JsonProperty("id") val id: Long?,
    @param:JsonProperty("name") val name: String,
    @param:JsonProperty("background") val background: String?,
    @param:JsonProperty("photos") var photos: Set<AdminArtistPhotoV2Dto> = setOf(),
    @param:JsonProperty("logEntries") val logEntries: List<AdminArtistLogEntryV2Dto> = listOf()
)

data class AdminArtistPhotoV2Dto @JsonCreator constructor(
    @param:JsonProperty("url") val url: URI,
    @param:JsonProperty("attribution") val attribution: String
)

class AdminArtistLogEntryV2Dto @JsonCreator constructor(
    @param:JsonProperty("date") val date: Instant,
    @param:JsonProperty("username") val username: String,
    @param:JsonProperty("userId") val userId: String?,
    @param:JsonProperty("httpMethod") val httpMethod: String?,
    @param:JsonProperty("request") val request: String?
)
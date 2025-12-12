package nl.orangeflamingo.voornameninliedjesbackend.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class TestSongStatisticsDto @JsonCreator constructor(
    @param:JsonProperty("status") val status: String,
    @param:JsonProperty("count") val count: Int
)
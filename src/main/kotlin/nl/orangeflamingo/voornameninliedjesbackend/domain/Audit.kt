package nl.orangeflamingo.voornameninliedjesbackend.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

data class Audit(

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Europe/Amsterdam")
        val dateInserted: Instant = Instant.now(),

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Europe/Amsterdam")
        val dateModified: Instant = dateInserted,

        val userInserted: String,
        val userModified: String = userInserted
)
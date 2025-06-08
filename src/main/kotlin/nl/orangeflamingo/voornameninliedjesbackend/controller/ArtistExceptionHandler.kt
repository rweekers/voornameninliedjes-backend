package nl.orangeflamingo.voornameninliedjesbackend.controller

import java.time.OffsetDateTime
import nl.orangeflamingo.voornameninliedjesbackend.model.ErrorResponse
import nl.orangeflamingo.voornameninliedjesbackend.service.DuplicateArtistNameException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ArtistExceptionHandler {

    @ExceptionHandler(DuplicateArtistNameException::class)
    fun handleDuplicateArtistName(
        ex: DuplicateArtistNameException
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            OffsetDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.reasonPhrase,
            ex.message ?: "Duplicate artist name"
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

}
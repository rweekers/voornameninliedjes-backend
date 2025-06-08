package nl.orangeflamingo.voornameninliedjesbackend.service

open class NotFoundException(override val message: String?): RuntimeException(message)
class SongNotFoundException(override val message: String?): NotFoundException(message)
class ArtistNotFoundException(override val message: String?): NotFoundException(message) {
    constructor(id: Long): this("Artist with id $id not found")
}
class DuplicateArtistNameException(override val message: String?): RuntimeException(message)

class LastFmException(override val message: String?): RuntimeException(message)
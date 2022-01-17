package nl.orangeflamingo.voornameninliedjesbackend.service

open class NotFoundException(override val message: String?): RuntimeException(message)
class SongNotFoundException(override val message: String?): NotFoundException(message)
class ArtistNotFoundException(override val message: String?): NotFoundException(message)

class LastFmException(override val message: String?): RuntimeException(message)
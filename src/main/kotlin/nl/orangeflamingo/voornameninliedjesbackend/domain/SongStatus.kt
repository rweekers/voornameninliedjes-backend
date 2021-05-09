package nl.orangeflamingo.voornameninliedjesbackend.domain

enum class SongStatus(val code: String) {

    SHOW("SHOW"),
    IN_PROGRESS("IN_PROGRESS"),
    TO_BE_DELETED("TO_BE_DELETED")
}
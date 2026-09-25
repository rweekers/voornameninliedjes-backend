package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.CurrentUser


fun interface CurrentUserService {
    fun currentUser(): CurrentUser
}
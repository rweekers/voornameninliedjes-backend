package nl.orangeflamingo.voornameninliedjesbackend.service

import org.springframework.stereotype.Service

@Service
class CurrentUserServiceImpl : CurrentUserService {
    override fun username(): String {
        return "test"
    }
}
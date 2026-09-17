package nl.orangeflamingo.voornameninliedjesbackend.service

import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class SpringHttpRequestContext : HttpRequestContext {

    override fun method(): String {
        return RequestContextHolder.currentRequestAttributes()
            .let { it as ServletRequestAttributes }
            .request
            .method
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.config

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository: ServerSecurityContextRepository {

    @Override
    override fun save(serverWebExchange: ServerWebExchange, securityContext: SecurityContext): Mono<Void> {
        // Don't know yet where this is for.
        return Mono.empty()
    }

    @Override
    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext>  {
        // JwtAuthenticationToken and GuestAuthenticationToken are custom Authentication tokens.
//        Authentication authentication = (/* check if authenticated based on headers in serverWebExchange */) ?
//        new JwtAuthenticationToken(...) :
//        new GuestAuthenticationToken();
        return Mono.just(SecurityContextImpl(UsernamePasswordAuthenticationToken(null, null)))
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository


@Configuration
class SecurityConfiguration {

    @Autowired
    private lateinit var authenticationManager: ReactiveAuthenticationManager

    @Autowired
    private lateinit var securityContextRepository: ServerSecurityContextRepository

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // Disable default security.
        http.httpBasic().disable()
        http.formLogin().disable()
        http.csrf().disable()
        http.logout().disable()

        // Add custom security.
        http.authenticationManager(this.authenticationManager)
        http.securityContextRepository(this.securityContextRepository)

        // Disable authentication for `/auth/**` routes.
        http.authorizeExchange().pathMatchers("/api/**").permitAll()
        http.authorizeExchange().anyExchange().authenticated()

        return http.build()
    }
}
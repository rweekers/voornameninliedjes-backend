package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.MyUserPrincipal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.HttpSecurityBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val userRepository: UserRepository,
    private val authenticationEntryPoint: MyBasicAuthPoint
) {
    @Bean
    fun filterChain(
        http: HttpSecurity
    ): SecurityFilterChain {
        http
            .cors(withDefaults())
            .csrf { it.disable() }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/admin/authenticate").permitAll()
                    .requestMatchers(HttpMethod.POST).authenticated()
                    .requestMatchers(HttpMethod.PUT).authenticated()
                    .requestMatchers(HttpMethod.PATCH).authenticated()
                    .requestMatchers(HttpMethod.DELETE).authenticated()
                    .requestMatchers("/admin/**").authenticated()
                    .anyRequest().permitAll()
            }
            .httpBasic(withDefaults())
        return http.build()
    }

    @Bean
    fun httpBasicConfigurer(): HttpBasicConfigurer<out HttpSecurityBuilder<*>> {
        return HttpBasicConfigurer()
            .authenticationEntryPoint(authenticationEntryPoint)
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
            MyUserPrincipal(user)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}
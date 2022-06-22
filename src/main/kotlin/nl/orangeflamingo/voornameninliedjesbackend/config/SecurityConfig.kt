package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.repository.postgres.UserRepository
import nl.orangeflamingo.voornameninliedjesbackend.service.MyUserPrincipal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val userRepository: UserRepository,
    private val authenticationEntryPoint: MyBasicAuthPoint
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.authenticationProvider(authenticationProvider())

        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/**", "/beta/**").permitAll()
            .and()
            .authenticationManager(authenticationManagerBuilder.build())
            .httpBasic()
            .authenticationEntryPoint(authenticationEntryPoint)
        return http.build()
    }

    private fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
            MyUserPrincipal(user)
        }
    }

    private fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
package nl.orangeflamingo.voornameninliedjesbackend.config

import nl.orangeflamingo.voornameninliedjesbackend.service.MyUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var authenticationEntryPoint: MyBasicAuthPoint

    @Autowired
    private lateinit var myUserDetailsService: MyUserDetailsService

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
            .authenticationProvider(authenticationProvider())
    }

    override fun configure(http: HttpSecurity) {
        http
            // TODO enable CSRF, see https://docs.spring.io/spring-security/site/docs/4.0.x/reference/htmlsingle/#csrf-configure
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/**").permitAll()
            .and()
            .httpBasic()
            .authenticationEntryPoint(authenticationEntryPoint)
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(myUserDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
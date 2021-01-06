package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class MyUserPrincipal(
        private val user: User
) : UserDetails {
    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.roles.map { SimpleGrantedAuthority("ROLE_$it") }.toMutableList()
    }
}
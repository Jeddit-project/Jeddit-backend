package com.jeddit.backend.authentification.security

import java.util.Date

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Created by stephan on 20.03.16.
 */

data class JwtUser(val id: Long,
                   private val username: String,
                   val first_name: String,
                   val last_name: String,
                   private val password: String): UserDetails {

    override fun getUsername(): String {
        return username
    }

    override fun getPassword(): String {
        return password
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getAuthorities(): List<GrantedAuthority> {
        return listOf()
    }

    override fun isEnabled(): Boolean {
        return true
    }
}

//class JwtUser(
//        @get:JsonIgnore
//        val id: Long?,
//        private val username: String,
//        val first_name: String,
//        val last_name: String,
//        val email: String,
//        private val password: String, private val authorities: Collection<GrantedAuthority>,
//        private val enabled: Boolean,
//        @get:JsonIgnore
//        val lastPasswordResetDate: Date
//) : UserDetails {
//
//    override fun getUsername(): String {
//        return username
//    }
//
//    @JsonIgnore
//    override fun isAccountNonExpired(): Boolean {
//        return true
//    }
//
//    @JsonIgnore
//    override fun isAccountNonLocked(): Boolean {
//        return true
//    }
//
//    @JsonIgnore
//    override fun isCredentialsNonExpired(): Boolean {
//        return true
//    }
//
//    @JsonIgnore
//    override fun getPassword(): String {
//        return password
//    }
//
//    override fun getAuthorities(): Collection<GrantedAuthority> {
//        return authorities
//    }
//
//    override fun isEnabled(): Boolean {
//        return enabled
//    }
//}

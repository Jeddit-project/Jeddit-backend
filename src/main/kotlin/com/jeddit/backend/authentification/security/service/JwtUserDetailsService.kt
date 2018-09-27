package com.jeddit.backend.authentification.security.service

import com.jeddit.backend.authentification.security.JwtUserDTO
import com.jeddit.backend.authentification.security.JwtUserFactory
import com.jeddit.backend.models.User
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val row = try {
            transaction {
                User.select{ User.username eq username }.first()
            }
        } catch (e: NoSuchElementException) {
            null
        }

        return if (row == null) {
            throw UsernameNotFoundException(String.format("No user found with username '%s'.", username))

        } else {
            val userPojo = transaction {
                JwtUserDTO.wrapRow(row).toPOJO()
            }

            JwtUserFactory.create(userPojo)
        }
    }
}

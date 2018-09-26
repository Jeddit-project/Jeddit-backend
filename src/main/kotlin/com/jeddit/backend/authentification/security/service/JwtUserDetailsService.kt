package com.jeddit.backend.authentification.security.service

import com.jeddit.backend.authentification.security.JwtUserDTO
import com.jeddit.backend.authentification.security.JwtUserFactory
import com.jeddit.backend.models.User
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
//import org.zerhusen.model.security.User
//import org.zerhusen.security.JwtUserFactory
//import org.zerhusen.security.repository.UserRepository

@Service
class JwtUserDetailsService : UserDetailsService {

//    @Autowired
//    private val userRepository: UserRepository? = null

    // 1 after authentificate

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val row = try {
            transaction {
                User.select{ User.username eq username }.first()
            }
        } catch (e: NoSuchElementException) {
            null
        }

//        val user = userRepository!!.findByUsername(username)

        return if (row == null) {
            throw UsernameNotFoundException(String.format("No user found with username '%s'.", username))
        } else {
            println("WORKS")

            val userPojo = transaction {
                JwtUserDTO.wrapRow(row).toPOJO()
            }

            JwtUserFactory.create(userPojo)
        }
    }
}

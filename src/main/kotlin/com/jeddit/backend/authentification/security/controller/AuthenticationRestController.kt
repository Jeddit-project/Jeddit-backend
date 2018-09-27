package com.jeddit.backend.authentification.security.controller

import com.jeddit.backend.authentification.security.JwtAuthenticationRequest
import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.authentification.security.JwtUser
import com.jeddit.backend.authentification.security.service.JwtAuthenticationResponse
import java.util.Objects
import javax.servlet.http.HttpServletRequest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*

@RestController
class AuthenticationRestController {

    @Value("\${jwt.header}")
    private val tokenHeader: String? = null

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val jwtTokenUtil: JwtTokenUtil? = null

//    @Autowired
//    @Qualifier("jwtUserDetailsService")
//    private val userDetailsService: UserDetailsService? = null

//    @CrossOrigin(origins = arrayOf("*"))
    @RequestMapping(value = "\${jwt.route.authentication.path}", method = arrayOf(RequestMethod.POST))
    @Throws(AuthenticationException::class)
    fun createAuthenticationToken(@RequestBody authenticationRequest: JwtAuthenticationRequest): ResponseEntity<*> {

        println(authenticationRequest.username)
        println(authenticationRequest.password)

        authenticate(authenticationRequest.username!!, authenticationRequest.password!!)

        // Reload password post-security so we can generate the token
//        val userDetails = userDetailsService!!.loadUserByUsername(authenticationRequest.username)
        val token = jwtTokenUtil!!.generateToken(authenticationRequest.username!!)

        // Return the token
        return ResponseEntity.ok<Any>(JwtAuthenticationResponse(token))
    }

//    @CrossOrigin(origins = arrayOf("*"))
    @RequestMapping(value = "\${jwt.route.authentication.refresh}", method = arrayOf(RequestMethod.GET))
    fun refreshAndGetAuthenticationToken(request: HttpServletRequest): ResponseEntity<*> {
        val authToken = request.getHeader(tokenHeader)
        val token = authToken.substring(7)
        val username = jwtTokenUtil!!.getUsernameFromToken(token)
//        val user = userDetailsService!!.loadUserByUsername(username) as JwtUser

//        if (jwtTokenUtil!!.canTokenBeRefreshed(token, user.lastPasswordResetDate)) {
        val refreshedToken = jwtTokenUtil!!.refreshToken(token)
        return ResponseEntity.ok<Any>(JwtAuthenticationResponse(refreshedToken))
//        } else {
//            return ResponseEntity.badRequest().body<Any>(null)
//        }
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)
    }

    /**
     * Authenticates the user. If something is wrong, an [AuthenticationException] will be thrown
     */
    private fun authenticate(username: String, password: String) {
        Objects.requireNonNull(username)
        Objects.requireNonNull(password)

        try {
            authenticationManager!!.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw AuthenticationException("User is disabled!", e)
        } catch (e: BadCredentialsException) {
            throw AuthenticationException("Bad credentials!", e)
        }

    }
}

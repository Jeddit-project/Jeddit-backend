package com.jeddit.backend.authentification.security

import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import java.io.Console
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

//import io.jsonwebtoken.ExpiredJwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizationTokenFilter(@Qualifier("jwtUserDetailsService") private val userDetailsService: UserDetailsService, private val jwtTokenUtil: JwtTokenUtil, @param:Value("\${jwt.header}") private val tokenHeader: String) : OncePerRequestFilter() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
//        logger.debug("processing authentication for '{}'", request.requestURL)

        val requestHeader = request.getHeader(this.tokenHeader)

        var username: String? = null
        var authToken: String? = null
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7)
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken)
            } catch (e: IllegalArgumentException) {
//                logger.error("an error occured during getting username from token", e)
            } catch (e: ExpiredJwtException) {
//                logger.warn("the token is expired and not valid anymore", e)
            }

        } else {
//            logger.warn("couldn't find bearer string, will ignore the header")
        }

//        println("MIGHT WORK $username")


//        logger.debug("checking authentication for user '{}'", username)
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
//            logger.debug("security context was null, so authorizating user")

            // It is not compelling necessary to load the use details from the database. You could also store the information
            // in the token and read it from it. It's up to you ;)
            val userDetails = this.userDetailsService.loadUserByUsername(username)

            // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
            // the database compellingly. Again it's up to you ;)

            if (jwtTokenUtil.validateToken(authToken!!, userDetails)!!) {
                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
//                logger.info("authorizated user '{}', setting security context", username)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        chain.doFilter(request, response)
    }
}

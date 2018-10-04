package com.jeddit.backend.authentification.security

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.jeddit.backend.models.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.DefaultClock
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*
import java.util.function.Function
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenUtil : Serializable {
    //    @SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "It's okay here")
    private val clock = DefaultClock.INSTANCE

    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Value("\${jwt.expiration}")
    private val expiration: Long? = null

    @Value("\${jwt.header}")
    lateinit var tokenHeader: String

    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token, Function { it.subject })
    }

    fun getIssuedAtDateFromToken(token: String): Date {
        return getClaimFromToken(token, Function { it.issuedAt })
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token, Function { it.expiration })
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .body
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(clock.now())
    }

    private fun isCreatedBeforeLastPasswordReset(created: Date, lastPasswordReset: Date?): Boolean {
        return lastPasswordReset != null && created.before(lastPasswordReset)
    }

    private fun ignoreTokenExpiration(token: String): Boolean {
        // here you specify tokens, for that the expiration is ignored
        return false
    }

//    fun generateToken(userDetails: UserDetails): String {
//        val claims = HashMap<String, Any>()
//        return doGenerateToken(claims, userDetails.username)
//    }

    // roscale
    fun getUsernameFromRequest(request: HttpServletRequest): String {
        val authToken = request.getHeader(tokenHeader)
        val token = authToken.substring(7)
        return getUsernameFromToken(token)
    }

    fun getUsernameFromRequestNullable(request: HttpServletRequest): String? {
        val authToken = request.getHeader(tokenHeader) ?: return null
        val token = authToken.substring(7)
        return getUsernameFromToken(token)
    }

    fun generateToken(username: String): String {
        val claims = HashMap<String, Any>()

        val id = transaction {
            User.select { User.username eq username }.first()[User.id].value
        }

        claims["id"] = id
        return doGenerateToken(claims, username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        val createdDate = clock.now()
        val expirationDate = calculateExpirationDate(createdDate)

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    fun canTokenBeRefreshed(token: String, lastPasswordReset: Date): Boolean {
        val created = getIssuedAtDateFromToken(token)
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && (!isTokenExpired(token) || ignoreTokenExpiration(token))
    }

    fun refreshToken(token: String): String {
        val createdDate = clock.now()
        val expirationDate = calculateExpirationDate(createdDate)

        val claims = getAllClaimsFromToken(token)
        claims.issuedAt = createdDate
        claims.expiration = expirationDate

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean? {
        val user = userDetails as JwtUser
        val username = getUsernameFromToken(token)
        val created = getIssuedAtDateFromToken(token)
        //final Date expiration = getExpirationDateFromToken(token);
        return (username == user.username
                && !isTokenExpired(token))
//                && !isCreatedBeforeLastPasswordReset(created, user.lastPasswordResetDate))
    }

    private fun calculateExpirationDate(createdDate: Date): Date {
        return Date(createdDate.time + expiration!! * 1000)
    }

    companion object {

        internal val CLAIM_KEY_USERNAME = "sub"
        internal val CLAIM_KEY_CREATED = "iat"
        private const val serialVersionUID = -3301605591108950415L
    }
}

package com.jeddit.backend.controllers

import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.models.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.security.crypto.bcrypt.BCrypt



class ValidationStatus {
    companion object {
        const val INVALID = "INVALID"
        const val ALREADY_USED = "ALREADY_USED"
        const val OK = "OK"
    }
}

data class RegistrationForm(val first_name: String, val last_name: String, val email: String, val username: String, val password: String, val confirmation_password: String)
data class RegistrationResponse(val success: Boolean, val token: String?)

@RestController
class RegistrationController {
    @GetMapping("/api/registration/validate_name")
    fun validateName(@RequestParam name: String): String {
        if (!name.contains(Regex("^[a-zA-Z ,.'-]+$"))) {
            return ValidationStatus.INVALID
        }
        return ValidationStatus.OK
    }

    @GetMapping("/api/registration/validate_email")
    fun validateEmail(@RequestParam email: String): String {
        if (!email.contains(Regex("^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)\$"))) {
            return ValidationStatus.INVALID
        }
        if (!transaction { User.select { User.email eq email }.count() == 0 }) {
            return ValidationStatus.ALREADY_USED
        }
        return ValidationStatus.OK
    }

    @GetMapping("/api/registration/validate_username")
    fun validateUsername(@RequestParam username: String): String {
        if (!username.contains(Regex("^[a-zA-Z0-9]+([_-]?[a-zA-Z0-9])*\$"))) {
            return ValidationStatus.INVALID
        }
        if (!transaction { User.select { User.username eq username }.count() == 0 }) {
            return ValidationStatus.ALREADY_USED
        }
        return ValidationStatus.OK
    }


    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val jwtTokenUtil: JwtTokenUtil? = null

    @PostMapping("/api/registration/register")
    fun register(@RequestBody registrationForm: RegistrationForm): RegistrationResponse {
        if ((validateName(registrationForm.first_name) == ValidationStatus.OK) &&
                (validateName(registrationForm.last_name) == ValidationStatus.OK) &&
                (validateEmail(registrationForm.email) == ValidationStatus.OK) &&
                (validateUsername(registrationForm.username) == ValidationStatus.OK) &&
                (registrationForm.password.length in 8..20) &&
                (registrationForm.password == registrationForm.confirmation_password)) {

            val hash = BCrypt.hashpw(registrationForm.password, BCrypt.gensalt())

            transaction {
                User.insert {
                    it[first_name] = registrationForm.first_name
                    it[last_name] = registrationForm.last_name
                    it[email] = registrationForm.email
                    it[username] = registrationForm.username
                    it[password_hash] = hash
                }
            }

            with(registrationForm) {
                authenticationManager!!.authenticate(UsernamePasswordAuthenticationToken(username, password))
                val token = jwtTokenUtil!!.generateToken(username)

                return RegistrationResponse(true, token)
            }
        }

        return RegistrationResponse(false, null)
    }
}
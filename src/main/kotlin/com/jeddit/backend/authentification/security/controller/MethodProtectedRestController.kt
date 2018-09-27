package com.jeddit.backend.authentification.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("protected")
class MethodProtectedRestController {

    /**
     * This is an example of some different kinds of granular restriction for endpoints. You can use the built-in SPEL expressions
     * in @PreAuthorize such as 'hasRole()' to determine if a user has access. Remember that the hasRole expression assumes a
     * 'ROLE_' prefix on all role names. So 'ADMIN' here is actually stored as 'ROLE_ADMIN' in database!
     */
    val protectedGreeting: ResponseEntity<*>
        @RequestMapping(method = arrayOf(RequestMethod.GET))
        @PreAuthorize("hasRole('ADMIN')")
        get() = ResponseEntity.ok("Greetings from admin protected method!")

}
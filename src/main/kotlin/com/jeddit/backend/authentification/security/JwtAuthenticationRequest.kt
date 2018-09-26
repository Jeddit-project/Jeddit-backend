package com.jeddit.backend.authentification.security

import java.io.Serializable

/**
 * Created by stephan on 20.03.16.
 */
class JwtAuthenticationRequest : Serializable {

    var username: String? = null
    var password: String? = null

    constructor() : super() {}

    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }

    companion object {

        private const val serialVersionUID = -8445943548965154778L
    }
}

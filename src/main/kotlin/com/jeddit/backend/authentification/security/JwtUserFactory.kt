package com.jeddit.backend.authentification.security

import com.jeddit.backend.models.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import java.util.stream.Collectors

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.zerhusen.model.security.Authority
//import org.zerhusen.model.security.User

data class JwtUserPOJO(val id: Long,
                       val username: String,
                       val first_name: String,
                       val last_name: String,
                       val password_hash: String)

class JwtUserDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<JwtUserDTO>(User)

    var username by User.username
    var first_name by User.first_name
    var last_name by User.last_name
    var password_hash by User.password_hash

    fun toPOJO(): JwtUserPOJO = JwtUserPOJO(id.value, username, first_name, last_name, password_hash)
}


object JwtUserFactory {

    fun create(user: JwtUserPOJO): JwtUser {
        return JwtUser(
                user.id,
                user.username,
                user.first_name,
                user.last_name,
                user.password_hash
//                mapToGrantedAuthorities(user.getAuthorities()),
//                user.getEnabled(),
//                user.getLastPasswordResetDate()
        )
    }

//    private fun mapToGrantedAuthorities(authorities: List<Authority>): List<GrantedAuthority> {
//        return authorities.stream()
//                .map { authority -> SimpleGrantedAuthority(authority.getName().name()) }
//                .collect<List<GrantedAuthority>, Any>(Collectors.toList())
//    }
}

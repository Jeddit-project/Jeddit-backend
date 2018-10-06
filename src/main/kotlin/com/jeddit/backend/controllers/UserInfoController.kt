package com.jeddit.backend.controllers

import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.models.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


data class UserInfoPOJO(val username: String, val first_name: String, val last_name: String)
class UserInfoDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<UserInfoDTO>(User)

    val username by User.username
    val first_name by User.first_name
    val last_name by User.last_name

    fun toPOJO(): UserInfoPOJO = UserInfoPOJO(username, first_name, last_name)
}


@RestController
class UserInfoController {
    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @GetMapping("/api/user/{username}/info")
    fun getUserInfo(@PathVariable username: String): UserInfoPOJO? {
        return transaction {
            val row = User.select { User.username eq username }.firstOrNull()

            if (row != null) {
                return@transaction UserInfoDTO.wrapRow(row).toPOJO()
            }
            return@transaction null
        }
    }

    @GetMapping("/api/user/me/info")
    fun getMyInfo(request: HttpServletRequest): UserInfoPOJO? {
        val username = jwtTokenUtil.getUsernameFromRequest(request) ?: return null
        return getUserInfo(username)
    }
}
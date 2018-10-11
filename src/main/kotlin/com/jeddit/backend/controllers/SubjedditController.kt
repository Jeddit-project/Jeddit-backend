package com.jeddit.backend.controllers

import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.models.Post
import com.jeddit.backend.models.Subjeddit
import com.jeddit.backend.models.Subscription
import com.jeddit.backend.models.User
import com.jeddit.backend.repositories.UserRepo
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


data class SubjedditInfoPOJO(val id: Long, val name: String, val image: String, val description: String, var subscribed: Boolean = false, var subscribers: Int = 0)
class SubjedditInfoDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<SubjedditInfoDTO>(Subjeddit)

    var name by Subjeddit.name
    var image by Subjeddit.image
    var description by Subjeddit.description

    fun toPOJO(): SubjedditInfoPOJO = SubjedditInfoPOJO(id.value, name, image, description)
}


@RestController
class SubjedditController {

    @Value("\${jwt.header}")
    lateinit var tokenHeader: String

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @GetMapping("/api/subjeddit/{name}/info")
    fun info(request: HttpServletRequest, @PathVariable name: String): SubjedditInfoPOJO {
        val username = jwtTokenUtil.getUsernameFromRequest(request)

        return transaction {
            val subscribers = (Subscription leftJoin Subjeddit).select { Subjeddit.name eq name }.count()

            val row = Subjeddit.select { Subjeddit.name eq name }.first()
            val pojo = SubjedditInfoDTO.wrapRow(row).toPOJO()
            pojo.subscribers = subscribers

            if (username != null) {
                pojo.subscribed = ! (Subscription leftJoin User leftJoin Subjeddit).select {
                    (User.username eq username) and (Subjeddit.name eq name)
                }.empty()
            }

            pojo
        }
    }

    @GetMapping("/api/subjeddit/{name}/posts")
    fun getPosts(request: HttpServletRequest, @PathVariable name: String): List<FeedPostPOJO> {
        val username = jwtTokenUtil.getUsernameFromRequest(request)

        return transaction {
            val query = (Post leftJoin Subjeddit).select { Subjeddit.name eq name }
            FeedPostDTO.wrapRows(query).toList().map {
                fillPost(it, username)
                it.toPostPOJO()
            }
        }
    }

    @PostMapping("/api/subjeddit/{name}/subscribe")
    fun subscribe(request: HttpServletRequest, @PathVariable name: String) {
        val username = jwtTokenUtil.getUsernameFromRequest(request) ?: return

        transaction {
            val alreadySubscribed = (Subscription leftJoin User leftJoin Subjeddit).select {
                (User.username eq username) and (Subjeddit.name eq name)
            }.count() > 0

            if (!alreadySubscribed) {
                Subscription.insert {
                    it[Subscription.user] = EntityID(UserRepo.getIdByUsername(username), User)
                    it[Subscription.subjeddit] = SubjedditInfoDTO.find { Subjeddit.name eq name }.first().id
                }
            }
        }
    }

    @PostMapping("/api/subjeddit/{name}/unsubscribe")
    fun unsubscribe(request: HttpServletRequest, @PathVariable name: String) {
        val username = jwtTokenUtil.getUsernameFromRequest(request) ?: return

        transaction {
            val alreadyUnsubscribed = (Subscription leftJoin User leftJoin Subjeddit).select {
                (User.username eq username) and (Subjeddit.name eq name)
            }.empty()

            if (!alreadyUnsubscribed) {
                Subscription.deleteWhere {
                    (Subscription.user eq EntityID(UserRepo.getIdByUsername(username), User)) and
                    (Subscription.subjeddit eq SubjedditInfoDTO.find { Subjeddit.name eq name }.first().id)
                }
            }
        }
    }
}
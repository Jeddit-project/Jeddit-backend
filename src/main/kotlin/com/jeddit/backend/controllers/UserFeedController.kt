package com.jeddit.backend.controllers

import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.models.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

data class FeedPostUserPOJO(val id: Long, val username: String)
class FeedPostUserDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<FeedPostUserDTO>(User)

    var username by User.username

    fun toPOJO(): FeedPostUserPOJO = FeedPostUserPOJO(id.value, username)
}

data class FeedPostSubjedditPOJO(val id: Long, val name: String, val image: String)
class FeedPostSubjedditDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<FeedPostSubjedditDTO>(Subjeddit)

    var name by Subjeddit.name
    var image by Subjeddit.image

    fun toPOJO(): FeedPostSubjedditPOJO = FeedPostSubjedditPOJO(id.value, name, image)
}

data class FeedPostPOJO(val id: Long, val title: String, val text: String, val points: Int, val comments: Int, val subjeddit: FeedPostSubjedditPOJO, val poster: FeedPostUserPOJO)
class FeedPostDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<FeedPostDTO>(Post)

    var title by Post.title
    var text by Post.text

    var points = 0
    var comments = 0

    var subjeddit by FeedPostSubjedditDTO referencedOn Post.subjeddit
    var poster by FeedPostUserDTO referencedOn Post.user

    fun toPostPOJO(): FeedPostPOJO = FeedPostPOJO(id.value, title, text, points, comments, subjeddit.toPOJO(), poster.toPOJO())
}


@RestController
class UserController {

    @Value("\${jwt.header}")
    lateinit var tokenHeader: String

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @GetMapping("/api/feed")
    fun getFeed(request: HttpServletRequest): List<FeedPostPOJO> {
        val authToken = request.getHeader(tokenHeader)
        val token = authToken.substring(7)
        val username = jwtTokenUtil.getUsernameFromToken(token)

        return transaction {
            val query = (Post leftJoin  User leftJoin Subjeddit leftJoin Subscription)
                    .select {
                        (User.username eq username)
                    }

            // Return
            FeedPostDTO.wrapRows(query).toList().map {

                val upvotes = (Post leftJoin PostVote leftJoin VoteType).select {
                    (PostVote.post eq it.id) and (VoteType.name eq "UPVOTE")
                }.count()

                val downvotes = (Post leftJoin PostVote leftJoin VoteType).select {
                    (PostVote.post eq it.id) and (VoteType.name eq "DOWNVOTE")
                }.count()

                it.points = upvotes - downvotes
                it.comments = (Post leftJoin Comment).select { Comment.post eq it.id }.count()

                it.toPostPOJO()
            }
        }
    }

    @PostMapping("/add")
    fun add(@RequestBody user: User): String {
        return "Saved"
    }
}
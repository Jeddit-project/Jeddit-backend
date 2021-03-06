package com.jeddit.backend.controllers

import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.models.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.JoinType
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

data class FeedPostPOJO(val id: Long, val created_at: Long, val updated_at: Long?, val random_id: String, val title_id: String, val title: String, val text: String, val points: Int, val vote: String, val comments: Int, val subjeddit: FeedPostSubjedditPOJO, val poster: FeedPostUserPOJO)
class FeedPostDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<FeedPostDTO>(Post)

    var created_at by Post.created_at
    var updated_at by Post.updated_at

    var random_id by Post.random_id
    var title_id by Post.title_id

    var title by Post.title
    var text by Post.text

    var points by Post.points
    var vote = "NONE"
    var comments by Post.comments

    var subjeddit by FeedPostSubjedditDTO referencedOn Post.subjeddit
    var poster by FeedPostUserDTO referencedOn Post.user

    fun toPostPOJO(): FeedPostPOJO = FeedPostPOJO(id.value, created_at, updated_at, random_id, title_id, title, text, points, vote, comments, subjeddit.toPOJO(), poster.toPOJO())
}


fun fillPost(it: FeedPostDTO, username: String?): FeedPostDTO {
    if (username != null) {
        val query = (PostVote leftJoin VoteType leftJoin User)
                .slice(VoteType.name)
                .select { (User.username eq username) and (PostVote.post eq it.id) }

        if (query.count() > 0) {
            it.vote = query.map { it[VoteType.name] }[0]
        }
    }

    return it
}


@RestController
class UserFeedController {

    @Value("\${jwt.header}")
    lateinit var tokenHeader: String

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @GetMapping("/api/feed")
    fun getFeed(request: HttpServletRequest, @RequestParam(defaultValue = "top") sort_by: String, @RequestParam(defaultValue = "0") offset: Int): List<FeedPostPOJO>? {
        val username = jwtTokenUtil.getUsernameFromRequest(request) ?: return null

        return transaction {
            // Don't link poster with subscription
            val query = (Post leftJoin Subjeddit).join(Subscription, JoinType.LEFT, additionalConstraint = {Post.subjeddit eq Subscription.subjeddit})
                    .join(User, JoinType.LEFT, additionalConstraint = {Subscription.user eq User.id})
                    .select { (User.username eq username) }
                    .orderBy(when (sort_by) {
                        "top" -> Pair(Post.points, false)
                        "new" -> Pair(Post.created_at, false)
                        else -> Pair(Post.points, false)
                    })
                    .limit(20, offset)

            // Return
            FeedPostDTO.wrapRows(query).toList().map {
                fillPost(it, username)
                it.toPostPOJO()
            }
        }
    }

    @GetMapping("api/post/{subjeddit_name}/{random_id}/{title_id}")
    fun getPost(request: HttpServletRequest, @PathVariable subjeddit_name: String, @PathVariable random_id: String, @PathVariable title_id: String): FeedPostPOJO? {
        val username = jwtTokenUtil.getUsernameFromRequest(request)

        return transaction { try {
            val query = (Post leftJoin Subjeddit leftJoin User)
                    .select { (Subjeddit.name eq subjeddit_name) and (Post.random_id eq random_id) and (Post.title_id eq title_id) }
                    .first()

            fillPost(FeedPostDTO.wrapRow(query), username).toPostPOJO()

        } catch (e: NoSuchElementException) { null } }
    }
}
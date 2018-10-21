package com.jeddit.backend.controllers

import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.models.Comment
import com.jeddit.backend.models.Post
import com.jeddit.backend.models.Subjeddit
import com.jeddit.backend.models.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


@RestController
class UserControler {

    data class PostPOJO(val id: Long, val title: String, val subjeddit: SubjedditPOJO, val poster: UserPOJO)
    class PostDTO(id: EntityID<Long>): LongEntity(id) {
        companion object : LongEntityClass<PostDTO>(Post)

        var title by Post.title
        var subjeddit by SubjedditDTO referencedOn Post.subjeddit
        var poster by UserDTO referencedOn Post.user

        fun toPostPOJO(): PostPOJO = PostPOJO(id.value, title, subjeddit.toPOJO(), poster.toPOJO())
    }

    data class UserPOJO(val id: Long, val username: String)
    class UserDTO(id: EntityID<Long>): LongEntity(id) {
        companion object : LongEntityClass<UserDTO>(User)

        var username by User.username

        fun toPOJO(): UserPOJO = UserPOJO(id.value, username)
    }

    data class SubjedditPOJO(val id: Long, val name: String)
    class SubjedditDTO(id: EntityID<Long>): LongEntity(id) {
        companion object : LongEntityClass<SubjedditDTO>(Subjeddit)

        var name by Subjeddit.name

        fun toPOJO(): SubjedditPOJO = SubjedditPOJO(id.value, name)
    }

    data class CommentPOJO(val id: Long, val created_at: Long, val updated_at: Long?, val text: String, val points: Int, val user: UserPOJO, val post: PostPOJO)
    class CommentDTO(id: EntityID<Long>): LongEntity(id) {
        companion object : LongEntityClass<CommentDTO>(Comment)

        var created_at by Comment.created_at
        var updated_at by Comment.updated_at

        var text by Comment.text
        var points by Comment.points

        var user by UserDTO referencedOn Comment.user
        var post by PostDTO referencedOn Comment.post

        fun toPOJO(): CommentPOJO = CommentPOJO(id.value, created_at, updated_at, text, points, user.toPOJO(), post.toPostPOJO())
    }


    @Value("\${jwt.header}")
    lateinit var tokenHeader: String

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @GetMapping("/api/user/{username}/posts")
    fun getUserPosts(request: HttpServletRequest,
                     @PathVariable username: String,
                     @RequestParam(defaultValue = "top") sort_by: String,
                     @RequestParam(defaultValue = "0") offset: Int): List<FeedPostPOJO> {

        val loggedUsername = jwtTokenUtil.getUsernameFromRequest(request)

        return transaction {
            val query = (Post leftJoin User)
                    .select { User.username eq username}
                    .orderBy(when (sort_by) {
                        "top" -> Pair(Post.points, false)
                        "new" -> Pair(Post.created_at, false)
                        else -> Pair(Post.points, false)
                    })
                    .limit(20, offset)

            FeedPostDTO.wrapRows(query).toList().map {
                fillPost(it, loggedUsername)
                it.toPostPOJO()
            }
        }
    }

    @GetMapping("/api/user/{username}/comments")
    fun getUserComments(@PathVariable username: String,
                        @RequestParam(defaultValue = "top") sort_by: String,
                        @RequestParam(defaultValue = "0") offset: Int): List<CommentPOJO> {

        return transaction {
            val query = (Comment leftJoin Post leftJoin Subjeddit).join(User, JoinType.LEFT, additionalConstraint = {Comment.user eq User.id })
                    .select { User.username eq username }
                    .orderBy(when (sort_by) {
                        "top" -> Pair(Comment.points, false)
                        "new" -> Pair(Comment.created_at, false)
                        else -> Pair(Comment.points, false)
                    })
                    .limit(20, offset)

            CommentDTO.wrapRows(query).toList().map {
                it.toPOJO()
            }
        }
    }
}
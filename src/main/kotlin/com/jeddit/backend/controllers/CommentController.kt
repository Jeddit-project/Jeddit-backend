package com.jeddit.backend.controllers

import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.models.*
import com.jeddit.backend.repositories.UserRepo
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.Instant
import javax.servlet.http.HttpServletRequest


data class CommentUserPOJO(val id: Long, val username: String)
class CommentUserDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<CommentUserDTO>(User)

    var username by User.username

    fun toPOJO(): CommentUserPOJO = CommentUserPOJO(id.value, username)
}

data class CommentPOJO(val id: Long, val created_at: Long, val updated_at: Long?, val text: String, val points: Int, val vote: String, val user: CommentUserPOJO, val replies: List<CommentPOJO>)
class CommentDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<CommentDTO>(Comment)

    var created_at by Comment.created_at
    var updated_at by Comment.updated_at

    var text by Comment.text
    var points: Int = 0
    var vote = "NONE"

    var user by CommentUserDTO referencedOn Comment.user
    var replies = listOf<CommentDTO>()

    fun toPOJO(): CommentPOJO = CommentPOJO(id.value, created_at, updated_at, text, points, vote, user.toPOJO(), replies.map { it.toPOJO() })

    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    updated_at = Instant.now().epochSecond
                } catch (e: Exception) {
                    //nothing much to do here
                }
            }
        }
    }
}


fun getTopComments(id: Long): List<CommentDTO> {
    return transaction {
        val query = (Comment leftJoin Post).select { Post.id eq id and Comment.comment.isNull() }
        CommentDTO.wrapRows(query).toList()
    }
}

fun populateReplies(comment: CommentDTO) {
    comment.replies = transaction {
        val query = Comment.select { Comment.comment eq comment.id }
        CommentDTO.wrapRows(query).toList()
    }

    comment.replies.forEach {
        populateReplies(it)
    }
}

fun calculatePoints(comment: CommentDTO) {
    comment.points = transaction {
        val upvotes = (CommentVote leftJoin VoteType).select { (CommentVote.comment eq comment.id) and (VoteType.name eq "UPVOTE") }.count()
        val downvotes = (CommentVote leftJoin VoteType).select { (CommentVote.comment eq comment.id) and (VoteType.name eq "DOWNVOTE") }.count()

        upvotes - downvotes
    }

    comment.replies.forEach {
        calculatePoints(it)
    }
}

fun setVote(comment: CommentDTO, username: String) {
    transaction {
        val query = (CommentVote leftJoin VoteType leftJoin User).slice(VoteType.name).select { (User.username eq username) and (CommentVote.comment eq comment.id) }
        if (query.count() > 0) {
            comment.vote = query.map { it[VoteType.name] }[0]
        }
    }

    comment.replies.forEach {
        setVote(it, username)
    }
}

@RestController
class CommentController {
    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @GetMapping("/api/post/{id}/comments")
    fun getComments(request: HttpServletRequest, @PathVariable id: Long): List<CommentPOJO> {
        val username = jwtTokenUtil.getUsernameFromRequest(request)

        return transaction {
            val topComments = getTopComments(id)

            topComments.forEach {
                populateReplies(it)
                calculatePoints(it)
                if (username != null) setVote(it, username)
            }

            topComments.map { it.toPOJO() }
        }
    }

    data class CommentRequestBody(val text: String, val parent: Long? = null)

    @PostMapping("/api/post/{id}/comments")
    fun comment(request: HttpServletRequest, @PathVariable id: Long, @RequestBody commentJson: CommentRequestBody) {
        val username = jwtTokenUtil.getUsernameFromRequest(request) ?: return
        val userId = UserRepo.getIdByUsername(username)

        transaction {
            Comment.insert {
                it[post] = EntityID(id, Post)
                it[comment] = if (commentJson.parent != null) EntityID(commentJson.parent, Comment) else null
                it[user] = EntityID(userId, User)
                it[text] = commentJson.text
            }
        }
    }
}
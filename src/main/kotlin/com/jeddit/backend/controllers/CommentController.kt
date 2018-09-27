package com.jeddit.backend.controllers

import com.jeddit.backend.models.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.Instant


data class CommentUserPOJO(val id: Long, val username: String)
class CommentUserDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<CommentUserDTO>(User)

    var username by User.username

    fun toPOJO(): CommentUserPOJO = CommentUserPOJO(id.value, username)
}

data class CommentPOJO(val id: Long, val created_at: Long, val updated_at: Long?, val text: String, val points: Int, val user: CommentUserPOJO, val replies: List<CommentPOJO>)
class CommentDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<CommentDTO>(Comment)

    var created_at by Comment.created_at
    var updated_at by Comment.updated_at

    var text by Comment.text
    var points: Int = 0

    var user by CommentUserDTO referencedOn Comment.user
    var replies = listOf<CommentDTO>()

    fun toPOJO(): CommentPOJO = CommentPOJO(id.value, created_at, updated_at, text, points, user.toPOJO(), replies.map { it.toPOJO() })

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

@RestController
class CommentController {

    @GetMapping("/api/post/{id}/comments")
    fun comments(@PathVariable id: Long): List<CommentPOJO> {

        return transaction {
            val topComments = getTopComments(id)

            topComments.forEach {
                populateReplies(it)
                calculatePoints(it)
            }

            topComments.map { it.toPOJO() }
        }
    }
}
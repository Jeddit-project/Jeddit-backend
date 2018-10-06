package com.jeddit.backend.controllers

import com.jeddit.backend.authentification.security.JwtTokenUtil
import com.jeddit.backend.models.*
import com.jeddit.backend.repositories.UserRepo
import com.jeddit.backend.repositories.VoteTypeRepo
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


data class Vote(val id: Long, val vote_type: String)


@RestController
class VoteController {
    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @PostMapping("/api/vote/post")
    fun votePost(request: HttpServletRequest, @RequestBody vote: Vote) {
        val username = jwtTokenUtil.getUsernameFromRequest(request) ?: return
        val userId = UserRepo.getIdByUsername(username)

        transaction {
            if (vote.vote_type == "NONE") {
                PostVote.deleteWhere { (PostVote.user eq userId) and (PostVote.post eq vote.id) }

            } else if (PostVote.select { (PostVote.user eq userId) and (PostVote.post eq vote.id) }.count() > 0) {
                PostVote.update({ (PostVote.user eq userId) and (PostVote.post eq vote.id) }) {
                    it[PostVote.vote_type] = EntityID(VoteTypeRepo.getIdByName(vote.vote_type), VoteType)
                }

            } else {
                PostVote.insert {
                    it[user] = EntityID(userId, User)
                    it[post] = EntityID(vote.id, Post)
                    it[vote_type] = EntityID(VoteTypeRepo.getIdByName(vote.vote_type), VoteType)
                }
            }
        }
    }

    @PostMapping("/api/vote/comment")
    fun voteComment(request: HttpServletRequest, @RequestBody vote: Vote) {
        val username = jwtTokenUtil.getUsernameFromRequest(request) ?: return
        val userId = UserRepo.getIdByUsername(username)

        transaction {
            if (vote.vote_type == "NONE") {
                CommentVote.deleteWhere { (CommentVote.user eq userId) and (CommentVote.comment eq vote.id) }

            } else if (CommentVote.select { (CommentVote.user eq userId) and (CommentVote.comment eq vote.id) }.count() > 0) {
                CommentVote.update({ (CommentVote.user eq userId) and (CommentVote.comment eq vote.id) }) {
                    it[CommentVote.vote_type] = EntityID(VoteTypeRepo.getIdByName(vote.vote_type), VoteType)
                }

            } else {
                CommentVote.insert {
                    it[user] = EntityID(userId, User)
                    it[comment] = EntityID(vote.id, Comment)
                    it[vote_type] = EntityID(VoteTypeRepo.getIdByName(vote.vote_type), VoteType)
                }
            }
        }
    }
}
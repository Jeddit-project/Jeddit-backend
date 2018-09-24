package com.jeddit.backend.models

import org.jetbrains.exposed.dao.LongIdTable


object CommentVote: LongIdTable() {
    val comment = reference("comment", Comment)
    val user = reference("user", User)
    val vote_type = reference("vote_type", VoteType)
}
package com.jeddit.backend.models

import org.jetbrains.exposed.dao.LongIdTable


object PostVote: LongIdTable() {
    val post = reference("post", Post)
    val user = reference("user", User)
    val vote_type = reference("vote_type", VoteType)
}
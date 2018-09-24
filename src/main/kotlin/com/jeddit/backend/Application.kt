package com.jeddit.backend

import com.jeddit.backend.models.Comment
import com.jeddit.backend.models.Post
import com.jeddit.backend.models.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.sql.Connection
import java.time.Instant
import java.util.*
import kotlin.system.exitProcess

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    Database.connect("jdbc:sqlite:./db.db", driver = "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
//        create(CommentVote, PostVote)

//        create(VoteType)
//
//        VoteType.insert {
//            it[name] = "UPVOTE"
//        }
//
//        VoteType.insert {
//            it[name] = "DOWNVOTE"
//        }

//        create(Comment)
//
//        Comment.insert {
//            it[text] = "So nice 2222"
//            it[user] = EntityID(1L, User)
//            it[post] = EntityID(2L, Post)
//            it[comment] = null
//        }

//        drop(User, Post, Subjeddit, Subscription)
//        create(User, Subjeddit, Post, Subscription)
//
//        User.insert {
//            it[username] = "roscaalex19"
//            it[first_name] = "Alex"
//            it[last_name] = "Rosca"
//            it[password_hash] = "1234"
//        }
//
//        Subjeddit.insert {
//            it[name] = "Terraria"
//            it[image] = "img"
//            it[description] = "A nice subjeddit"
//        }
//
//        Subscription.insert {
//            it[user] = EntityID(1L, User)
//            it[subjeddit] = EntityID(1L, Subjeddit)
//        }
    }

    runApplication<Application>(*args)
}

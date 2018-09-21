package com.jeddit.backend

import com.jeddit.backend.models.Post
import com.jeddit.backend.models.Subjeddit
import com.jeddit.backend.models.Subscription
import com.jeddit.backend.models.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    Database.connect("jdbc:mysql://www.db4free.net/redditdb", driver = "com.mysql.jdbc.Driver",
            user = "redditwow", password = "Romania12")

    transaction {
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

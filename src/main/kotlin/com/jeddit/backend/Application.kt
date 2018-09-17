package com.jeddit.backend

import com.jeddit.backend.models.Post
import com.jeddit.backend.models.Subjeddit
import com.jeddit.backend.models.Subscription
import com.jeddit.backend.models.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    Database.connect("jdbc:mysql://www.db4free.net/redditdb", driver = "com.mysql.jdbc.Driver",
            user = "redditwow", password = "Romania12")

    transaction {
        create(User, Subjeddit, Post, Subscription)

        User.insert {
            it[id] = UUID.randomUUID()
            it[first_name] = "Alex"
            it[last_name] = "Rosca"
            it[password_hash] = "1234"
        }
    }

    runApplication<Application>(*args)
}

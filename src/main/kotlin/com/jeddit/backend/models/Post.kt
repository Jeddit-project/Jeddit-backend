package com.jeddit.backend.models

import com.jeddit.backend.models.Comment.clientDefault
import org.jetbrains.exposed.dao.LongIdTable
import java.time.Instant


object Post: LongIdTable() {
    val created_at = long("created_at").clientDefault { Instant.now().epochSecond }
    val updated_at = long("updated_at").nullable()

    val random_id = varchar("random_id", 255)
    val title_id = varchar("title_id", 255)

    val title = varchar("title", 255)
    val text = text("text")
    val image = varchar("image", 255)
    val points = integer("points")
    val comments = integer("comments")

    val user = reference("user", User)
    val subjeddit = reference("subjeddit", Subjeddit)
}
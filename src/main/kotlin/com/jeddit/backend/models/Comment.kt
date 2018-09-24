package com.jeddit.backend.models

import org.jetbrains.exposed.dao.*
import java.time.Instant
import java.util.*


object Comment: LongIdTable() {
    val created_at = long("created_at").clientDefault { Instant.now().epochSecond }
    val updated_at = long("updated_at").nullable()

    val text = text("text")

    val user = reference("user", User)
    val post = reference("post", Post)
    val comment = reference("comment", Comment).nullable()
}
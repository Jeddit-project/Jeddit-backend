package com.jeddit.backend.models

import org.jetbrains.exposed.dao.LongIdTable


object Post: LongIdTable() {
    val title = varchar("title", 255)
    val text = text("text")
    val image = varchar("image", 255)
    val user = reference("user", User)
    val subjeddit = reference("subjeddit", Subjeddit)
}
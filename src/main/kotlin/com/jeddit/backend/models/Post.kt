package com.jeddit.backend.models

import org.jetbrains.exposed.sql.Table


object Post: Table() {
    val id = uuid("id").primaryKey()
    val title = varchar("title", 255)
    val text = text("text")
    val image = varchar("image", 255)
    val user_id = (uuid("user_id") references User.id)
    val subjeddit_id = (uuid("subjeddit_id") references Subjeddit.id)
}
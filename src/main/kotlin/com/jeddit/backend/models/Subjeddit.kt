package com.jeddit.backend.models

import org.jetbrains.exposed.sql.Table


object Subjeddit: Table() {
    val id = uuid("id").primaryKey()
    val name = varchar("name", 255)
    val image = varchar("image", 255)
    val description = text("description")
}

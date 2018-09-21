package com.jeddit.backend.models

import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Table


object Subjeddit: LongIdTable() {
    val name = varchar("name", 255)
    val image = varchar("image", 255)
    val description = text("description")
}

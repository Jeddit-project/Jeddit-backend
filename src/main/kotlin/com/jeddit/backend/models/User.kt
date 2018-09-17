package com.jeddit.backend.models

import org.jetbrains.exposed.sql.Table

object User: Table() {
    val id = uuid("id").primaryKey()
    val first_name = varchar("first_name", 255)
    val last_name = varchar("last_name", 255)
    val username = varchar("username", 255)
    val password_hash = varchar("password_hash", 64)
}
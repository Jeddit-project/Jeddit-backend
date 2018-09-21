package com.jeddit.backend.models

import org.jetbrains.exposed.dao.LongIdTable


object User: LongIdTable() {
    val first_name = varchar("first_name", 255)
    val last_name = varchar("last_name", 255)
    val username = varchar("username", 255)
    val password_hash = varchar("password_hash", 64)
}
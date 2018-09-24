package com.jeddit.backend.models

import org.jetbrains.exposed.dao.LongIdTable


object VoteType: LongIdTable() {
    val name = varchar("name", 255)
}
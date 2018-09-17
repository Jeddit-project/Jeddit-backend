package com.jeddit.backend.models

import org.jetbrains.exposed.sql.Table


object Subscription: Table() {
    val id = uuid("id").primaryKey()
    val user_id = uuid("user_id") references User.id
    val subjeddit_id = uuid("subjeddit_id") references Subjeddit.id
}
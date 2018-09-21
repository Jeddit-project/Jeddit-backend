package com.jeddit.backend.models

import org.jetbrains.exposed.dao.LongIdTable


object Subscription: LongIdTable() {
    val user = reference("user", User)
    val subjeddit = reference("subjeddit", Subjeddit)
}
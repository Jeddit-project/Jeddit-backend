package com.jeddit.backend.repositories

import com.jeddit.backend.models.User
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction


class UserRepo {
    companion object {
        fun getIdByUsername(username: String): Long =
                transaction { User.slice(User.id)
                        .select { User.username eq username }
                        .map { it[User.id] }[0].value }
    }
}

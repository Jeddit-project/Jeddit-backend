package com.jeddit.backend.repositories

import com.jeddit.backend.models.VoteType
import org.jetbrains.exposed.sql.select


class VoteTypeRepo {
    companion object {
        fun getIdByName(name: String): Long =
                VoteType.slice(VoteType.id).select { VoteType.name eq name }.map { it[VoteType.id] }[0].value
    }
}
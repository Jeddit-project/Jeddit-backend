package com.jeddit.backend.controllers

import com.jeddit.backend.models.Subjeddit
import com.jeddit.backend.models.Subscription
import com.jeddit.backend.models.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.*


data class SubjedditInfoPOJO(val id: Long, val name: String, val image: String, val description: String, var subscribers: Int = 0)
class SubjedditInfoDTO(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<SubjedditInfoDTO>(Subjeddit)

    var name by Subjeddit.name
    var image by Subjeddit.image
    var description by Subjeddit.description

    fun toPOJO(): SubjedditInfoPOJO = SubjedditInfoPOJO(id.value, name, image, description)
}


@CrossOrigin(origins = arrayOf("*"))
@RestController
class SubjedditController {

    @GetMapping("/api/subjeddit/{id}/info")
    fun info(@PathVariable id: Long): SubjedditInfoPOJO {
        return transaction {
            val subscribers = Subscription.select { Subscription.subjeddit eq id }.count()

            val row = Subjeddit.select { Subjeddit.id eq id }.first()
            val pojo = SubjedditInfoDTO.wrapRow(row).toPOJO()
            pojo.subscribers = subscribers

            pojo
        }
    }
}
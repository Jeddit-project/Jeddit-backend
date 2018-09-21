package com.jeddit.backend.controllers

import com.jeddit.backend.models.Post
import com.jeddit.backend.models.Subjeddit
import com.jeddit.backend.models.Subscription
import com.jeddit.backend.models.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.*

data class UserPOJO(val id: Long, val username: String)
class Userdd(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<Userdd>(User)

    var username by User.username

    fun toUserPOJO(): UserPOJO = UserPOJO(id.value, username)
}

data class SubjedditPOJO(val id: Long, val name: String, val image: String)
class Subjedditdd(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<Subjedditdd>(Subjeddit)

    var name by Subjeddit.name
    var image by Subjeddit.image

    fun toSubjedditPOJO(): SubjedditPOJO = SubjedditPOJO(id.value, name, image)
}

data class PostPOJO(val id: Long, val title: String, val text: String, val points: Int, val comments: Int, val subjeddit: SubjedditPOJO, val poster: UserPOJO)
class Postdd(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<Postdd>(Post)

    var title by Post.title
    var text by Post.text
    var points by Post.points
    var comments by Post.comments
    var subjeddit by Subjedditdd referencedOn Post.subjeddit
    var poster by Userdd referencedOn Post.user

    fun toPostPOJO(): PostPOJO = PostPOJO(id.value, title, text, points, comments, subjeddit.toSubjedditPOJO(), poster.toUserPOJO())
}


@CrossOrigin(origins = arrayOf("*"))
@RestController
class UserController {
//    @Autowired
//    lateinit var userRepository: UserRepository

    @GetMapping("/test")
    fun test(): List<PostPOJO> {
        return transaction {

            val fields = arrayListOf<Expression<*>>(
                    Post.id,
                    Post.title,
                    Post.text,
                    Post.image,
                    Post.user,
                    Post.subjeddit,
//                    Subjeddit.name,
                    User.id,
                    User.first_name,
                    User.last_name,
                    User.username,

                    Subjeddit.id,
                    Subjeddit.name,
                    Subjeddit.image
            )

            val query = (Post innerJoin User innerJoin Subjeddit innerJoin Subscription)
//                    .slice(fields)
                    .select {
                        (Subscription.user eq 1L) and (Subjeddit.id eq Subscription.subjeddit)
                    }
                    .withDistinct()

            // Return
            Postdd.wrapRows(query).toList().map {
                it.toPostPOJO()
            }
        }
    }

    @PostMapping("/add")
    fun add(@RequestBody user: User): String {
//        print("CREATING NEW USER ${user.username}")
//        userRepository.save(user)
        return "Saved"
    }
}
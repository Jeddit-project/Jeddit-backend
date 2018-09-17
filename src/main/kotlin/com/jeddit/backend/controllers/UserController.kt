package com.jeddit.backend.controllers

import com.jeddit.backend.models.User
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {
//    @Autowired
//    lateinit var userRepository: UserRepository

    @GetMapping("/test")
    fun test(): String {
        val user = transaction {
            return@transaction User.select {
                User.first_name eq "Alex"
            }.first()
        }

        return user[User.last_name]
    }

//    @GetMapping("/get")
//    fun get(): Iterable<User> {
//        return userRepository.findAll()
//    }

    @PostMapping("/add")
    fun add(@RequestBody user: User): String {
//        print("CREATING NEW USER ${user.username}")
//        userRepository.save(user)
        return "Saved"
    }
}
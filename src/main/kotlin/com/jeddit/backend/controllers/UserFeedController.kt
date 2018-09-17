package com.jeddit.backend.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


//data class FeedPost(val id: Long,
//                    val subjeddit: UserFeedSubjedditPOJO,
//                    val poster: UserFeedPosterPOJO,
//                    val title: String,
//                    val time: Long,
//                    val text_content: String,
//                    val points: Int,
//                    val comments: Int)


@RestController
class UserFeedController {
//    @Autowired
//    lateinit var subscriptionRepo: SubscriptionRepo

    @GetMapping("/api/user_feed")
    fun getUserFeed(): String {
//        val user = userRepository.findById(0).get()

//        return postRepo.findBySubjeddit(user.subscriptions!!)

//        return posts
        return "mere"
    }
}
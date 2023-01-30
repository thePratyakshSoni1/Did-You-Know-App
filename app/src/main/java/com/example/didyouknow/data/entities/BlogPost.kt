package com.example.didyouknow.data.entities

import java.util.*

data class BlogPost (
    val content:String="",
    val date: Date = Date(),
    val title:String="",
    val articleId:String="",
    val imageUrl:String="",
    val totalDislikes:Long=0L,
    val totalLikes:Long=0L
)
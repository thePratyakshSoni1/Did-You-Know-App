package com.example.didyouknow.data.entities

import java.sql.Timestamp
import java.util.*

data class BlogPost (
    val Content:String="",
    val Date: Date = Date(),
    val Title:String="",
    val articleId:String="",
    val imageUrl:String="",
    val totalDislikes:Long=0L,
    val totalLikes:Long=0L
)
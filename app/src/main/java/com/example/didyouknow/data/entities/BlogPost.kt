package com.example.didyouknow.data.entities

import java.sql.Timestamp

data class BlogPost (
    var Content:String,
    var Date:Timestamp,
    var Title:String,
    var articleId:String,
    var imageUrl:String,
    var totalDislikes:String,
    var totalLikes:String,

        )
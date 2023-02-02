package com.example.didyouknow.data.remote

import java.util.*

object FirebaseDocFieldNames {

    val BlogTitleField:String = "title"
    val BlogImageLinkField:String = "imageUrl"
    val BlogContentField:String = "content"
    val BlogDateField:String = "date"
    val BlogArticleIdField:String = "articleId"
    val BlogLikesField:String = "totalLikes"
    val BlogDislikesField:String = "totalDislikes"


}

enum class FirebaseDocFields {

    TITLE,
    CONTENT,
    IMAGE_URL,
    ARTICLE_ID,
    DATE,
    DISLIKES,
    LIKES

}
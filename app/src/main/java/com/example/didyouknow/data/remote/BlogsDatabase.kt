package com.example.didyouknow.data.remote

import android.util.Log
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.other.Resources
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BlogsDatabase {

    private val firebase = FirebaseFirestore.getInstance()
    private val fbCollection = firebase.collection("BlogPost1")

    suspend fun getAllBlogs():List<BlogPost>{
        return try{
            var results = fbCollection.get().await().toObjects(BlogPost::class.java)
            Log.d("BlogFbDatabaseLogs","resultsFetched after converting to objects: \n$results")
            Log.d("BlogFbDatabaseLogs","resultsFetched after converting to objects: \n$results")
            results
        }catch(e:Exception){
            Log.d("BlogFbDatabaseLogs","fetching failed ${e.message}")
            Log.d("BlogFbDatabaseLogs","fetching failed ${e.stackTrace}")
            emptyList()
        }
    }

    suspend fun getBlogById(blogId:String):BlogPost?{
        var requiredBlog:BlogPost? = null

        requiredBlog = fbCollection.document(blogId).get().await().toObject(BlogPost::class.java)
        Log.d("BlogFbDatabaseLogs", "After get call: ${requiredBlog?.title}")
        Log.d("BlogFbDatabaseLogs", "Returning $requiredBlog")
        return requiredBlog

    }

    suspend fun postBlog( blog:BlogPost ):Resources<Boolean>{

        val task = fbCollection.document(blog.articleId).set(blog)
        task.await()
        return if(task.isSuccessful){
            Resources.success(true)
        }else{
            Resources.error(false, "${task.exception?.message}\n${task.exception?.stackTrace}")
        }

    }

    suspend fun deleteBlogDoc( blogDocId:String ):Resources<Boolean>{

        val task = fbCollection.document(blogDocId).delete()
        task.await()
        return if(task.isSuccessful){
            Resources.success(true)
        }else{
            Resources.error(false, "${task.exception?.message}\n${task.exception?.stackTrace}")
        }

    }

    suspend fun updateBlogTitle( newTitle:String, articleId:String ):Resources<Boolean>{

        val docRef = fbCollection.document(articleId)
        val task = docRef.update(
            FirebaseDocFieldNames.BlogTitleField, newTitle
        )

        task.await()
        return if(task.isSuccessful){
            Resources.success(true)
        }else{
            Log.d("BlogFbDatabaseLogs", "${task.exception?.message}\n${task.exception?.stackTrace}")
            Resources.error(false, "Title not updated")
        }

    }
    suspend fun updateBlogContent( newContent:String, articleId:String ):Resources<Boolean>{

        val docRef = fbCollection.document(articleId)
        val task = docRef.update(
            FirebaseDocFieldNames.BlogContentField, newContent
        )

        task.await()
        return if(task.isSuccessful){
            Resources.success(true)
        }else{
            Log.d("BlogFbDatabaseLogs", "${task.exception?.message}\n${task.exception?.stackTrace}")
            Resources.error(false, "Content not updated")
        }

    }
    suspend fun updateBlogImage( newImgLink:String, articleId:String, imgName:String? ):Resources<Boolean>{

        val docRef = fbCollection.document(articleId)
        val task = docRef.update(
            FirebaseDocFieldNames.BlogImageLinkField, newImgLink
        )

        val taskName = docRef.update(
            FirebaseDocFieldNames.BlogsImageNameField, imgName
        )

        task.await()
        taskName.await()
        return if(task.isSuccessful){
            Resources.success(true)
        }else{
            Log.d("BlogFbDatabaseLogs", "${task.exception?.message}\n${task.exception?.stackTrace}")
            Resources.error(false, "Image not updated")
        }

    }


}
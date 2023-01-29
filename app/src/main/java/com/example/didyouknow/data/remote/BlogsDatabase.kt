package com.example.didyouknow.data.remote

import android.util.Log
import com.example.didyouknow.data.entities.BlogPost
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
        Log.d("BlogFbDatabaseLogs", "After get call: ${requiredBlog?.Title}")

//        fbCollection.document(blogId).get().addOnCompleteListener {
//
//            if(it.isSuccessful){
//                Log.d("BlogFbDatabaseLogs", "Success")
//                requiredBlog = it.result.toObject(BlogPost::class.java)
//                Log.d("BlogFbDatabaseLogs", "${requiredBlog?.Title}")
//            }else{
//                Log.d("BlogFbDatabaseLogs", "Fetching Unuccessfull")
//            }
//
//        }


        Log.d("BlogFbDatabaseLogs", "Returning $requiredBlog")
        return requiredBlog

    }

}
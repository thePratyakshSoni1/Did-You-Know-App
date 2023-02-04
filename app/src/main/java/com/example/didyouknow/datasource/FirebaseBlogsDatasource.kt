package com.example.didyouknow.datasource

import android.util.Log
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.data.remote.BlogsDatabase
import com.example.didyouknow.other.Resources
import javax.inject.Inject

class FirebaseBlogsDatasource @Inject constructor(
    val blogsDatabse:BlogsDatabase
){

    suspend  fun fetchAllBlogs(): Resources<List<BlogPost>> {

        val blogs = blogsDatabse.getAllBlogs()
        return if(blogs.isNotEmpty()){
            Log.d("FirebaseSourceLogs","Success Fetching: ${blogs}")
            Resources.success(blogs)
        }else {
            Resources.error(blogs, "Error fetching songs from firebase")
        }

    }

    suspend fun fetchBlogById(blogId:String):Resources<BlogPost?>{

        val requiredBlogPost:BlogPost = blogsDatabse.getBlogById(blogId)!!
         Log.d("FirebaseSourceLogs","Success Fetching: ${requiredBlogPost.title}")
        return Resources.success(requiredBlogPost)

    }

    suspend fun deleteBlogDoc( blogDocId:String ):Resources<Boolean> = blogsDatabse.deleteBlogDoc( blogDocId )
    suspend fun postBlog( blog:BlogPost ):Resources<Boolean> = blogsDatabse.postBlog(blog)

    suspend fun updateBlogTitle( newTitle:String, articleId:String ): Resources<Boolean> = blogsDatabse.updateBlogTitle(newTitle, articleId)
    suspend fun updateBlogContent( newContent:String, articleId:String ): Resources<Boolean> = blogsDatabse.updateBlogContent(newContent, articleId)
    suspend fun updateBlogImage( newImageLink:String, articleId:String ): Resources<Boolean> = blogsDatabse.updateBlogImage(newImageLink, articleId)


}
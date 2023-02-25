package com.example.didyouknow.datasource

import android.net.Uri
import android.util.Log
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.data.remote.BlogsDatabase
import com.example.didyouknow.data.remote.ImageStorageDatabase
import com.example.didyouknow.other.Resources
import com.example.didyouknow.other.Status
import javax.inject.Inject

class FirebaseBlogsDatasource @Inject constructor(
    val blogsDatabse:BlogsDatabase,
    val imageStorageDatabse:ImageStorageDatabase
){

    suspend  fun fetchAllBlogs(): Resources<List<BlogPost>> {

        val blogs = blogsDatabse.getAllBlogs().sortedByDescending {
            it.date
        }

        return if(blogs.isNotEmpty()){
            Log.d("FirebaseSourceLogs","Success Fetching: ${blogs}")
            Resources.success(blogs)
        }else {
            Resources.error(blogs, "Error fetching songs from firebase")
        }

    }

    suspend fun fetchBlogById(blogId:String):Resources<BlogPost?>{

        val requiredBlogPost:BlogPost? = blogsDatabse.getBlogById(blogId)
         Log.d("FirebaseSourceLogs","Success Fetching: ${requiredBlogPost?.title}")
        return Resources.success(requiredBlogPost)

    }

    suspend fun deleteBlogDoc( blogDocId:String ):Resources<Boolean> = blogsDatabse.deleteBlogDoc( blogDocId )
    suspend fun postBlog( blog:BlogPost ):Resources<Boolean> = blogsDatabse.postBlog(blog)

    suspend fun updateBlogTitle( newTitle:String, articleId:String ): Resources<Boolean> = blogsDatabse.updateBlogTitle(newTitle, articleId)
    suspend fun updateBlogContent( newContent:String, articleId:String ): Resources<Boolean> = blogsDatabse.updateBlogContent(newContent, articleId)
    suspend fun updateBlogImage( newImageLink:String, articleId:String, imageName:String? = null ): Resources<Boolean> = blogsDatabse.updateBlogImage(newImageLink, articleId, imageName)

    suspend fun uploadImageForBlog(uri:Uri):Resources<Pair<String,Uri>?>{
        return imageStorageDatabse.uploadImage(uri)
    }
    suspend fun deleteBlogImage(imageName:String):Resources<Boolean>{
        val taskResults = imageStorageDatabse.deleteBlogImage(imageName)
        Log.d("FirebaseBlogsSourceLogs",if(taskResults.status == Status.SUCCESS) "Delted blog image success fully" else "Blogs image cannot be deleted")
        return taskResults
    }

}
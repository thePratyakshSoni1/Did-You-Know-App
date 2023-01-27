package com.example.didyouknow.datasource

import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.data.remote.BlogsDatabase
import com.example.didyouknow.other.Event
import com.example.didyouknow.other.Resources
import com.example.didyouknow.other.Status
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class FirebaseBlogsDataSource @Inject constructor(
    val blogsDatabse:BlogsDatabase
){

    var blogs = emptyList<BlogPost>()
    var status: Status? = null

    suspend  fun fetchAllBlogs(){
        status = Status.LOADING
        try{
            blogs = blogsDatabse.getAllBlogs()
            status = Status.SUCCESS
        }catch (e:Exception){
            status = Status.ERROR
        }
    }


}
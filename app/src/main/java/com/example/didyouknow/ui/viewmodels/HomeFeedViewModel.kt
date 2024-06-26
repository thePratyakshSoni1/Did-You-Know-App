package com.example.didyouknow.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.datasource.FirebaseBlogsDatasource
import com.example.didyouknow.other.Resources
import com.example.didyouknow.other.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeFeedViewModel @Inject constructor (
    val blogsDataSource:FirebaseBlogsDatasource
): ViewModel() {

    val blogPosts: MutableLiveData<Resources<List<BlogPost>>> =
        MutableLiveData<Resources<List<BlogPost>>>()

    init {

        Log.d("HomeFeedViewModelLogs", "Initing VM")
        blogPosts.postValue(Resources.loading(emptyList()))

        viewModelScope.launch {
            fetchAndSyncBlogs()
        }

    }


    fun refreshBlogs(onSuccessRefresh:()->Unit){
        viewModelScope.launch {
            val result = blogsDataSource.fetchAllBlogs()
            blogPosts.postValue( result )
            onSuccessRefresh()
        }
    }

    private suspend fun fetchAndSyncBlogs() {

        Log.d("HomeFeedViewModelLogs", "Syncing blogs")
        var results = blogsDataSource.fetchAllBlogs()
        blogPosts.postValue(results)
        Log.d("HomeFeedViewModelLogs", "Bloogs fetched")

    }

    fun deleteBlog(blogId:String, imageName:String?):Resources<Boolean>{

        val status:Resources<Boolean>
        runBlocking {
            val blogDeleteTask = async { blogsDataSource.deleteBlogDoc( blogId ) }
            val imageDeleteTask = if( !imageName.isNullOrEmpty() ){
                async{ blogsDataSource.deleteBlogImage(imageName) }
            }else null

            status = blogDeleteTask.await()
            imageDeleteTask?.await() ?: Unit
        }
        return status


    }

}
package com.example.didyouknow.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.datasource.FirebaseBlogsDataSource
import com.example.didyouknow.other.Resources
import com.example.didyouknow.other.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFeedViewModel @Inject constructor (
    val blogsDataSource:FirebaseBlogsDataSource
): ViewModel() {

    val blogPosts: MutableLiveData<Resources<List<BlogPost>>> =
        MutableLiveData<Resources<List<BlogPost>>>()

    init {

        Log.d("HomeFeedViewModelLogs", "Initing VM")
        viewModelScope.launch {

            blogsDataSource.fetchAllBlogs()
            syncBlogsIfFetched()

        }


    }

    private fun syncBlogsIfFetched() {

        Log.d("HomeFeedViewModelLogs", "Syncing blogs")
        viewModelScope.launch {
            var songsSynced = false
            while (!songsSynced) {
                if (blogsDataSource.status == Status.SUCCESS) {
                    blogPosts.postValue(Resources.success(blogsDataSource.blogs))
                    songsSynced = true
                } else if (blogsDataSource.status == Status.ERROR) {
                    blogPosts.postValue(
                        Resources.error(
                            emptyList(),
                            "Error Fetching the blog posts !"
                        )
                    )
                    songsSynced = true
                }
                delay(300L)
            }

            Log.d("HomeFeedViewModelLogs", "Bloogs fetched")

        }


    }

}
package com.example.didyouknow.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.data.remote.BlogsDatabase
import com.example.didyouknow.datasource.FirebaseBlogsDatasource
import com.example.didyouknow.other.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel@Inject constructor(
    val blogsDatasource: FirebaseBlogsDatasource
): ViewModel() {

    private val _blogsResults = MutableLiveData<List<BlogPost>>()
    val blogsResults get() = _blogsResults as LiveData<List<BlogPost>>

    private val _allBlogs = mutableListOf<BlogPost>()
    val allBlogs get() = _allBlogs as List<BlogPost>

    private var _queryString = MutableLiveData<String>()
    val queryString get() = _queryString as LiveData<String>

    fun updateQueryString(newtxt:String) = _queryString.postValue(newtxt)

    init {
        viewModelScope.launch {
            refreshResults()
        }
    }


    private suspend fun refreshResults() {
        _allBlogs.clear()
        blogsDatasource.fetchAllBlogs().let {
            if (it.status == Status.SUCCESS) {
                _allBlogs.addAll(it.data)
                Log.d("SearchFragmentVMLogs", "Blogs updated: ${it.data.size}")
            } else {
                Log.d("SearchFragmentVMLogs", "Error fetching blogs")
            }
        }
        query()
    }

    fun query(){
        if(queryString.value?.isNotEmpty() == true){
            viewModelScope.launch {
                val results = allBlogs.filter {
                    it.title.contains(queryString.value!!, true) || it.content.contains(queryString.value!!, true)
                }

                results.let{
                    _blogsResults.postValue(it)
                    Log.d("SearchFragmentVMLogs", "Blogs search filtered: ${it.size} blogs found")
                }

            }
        }

    }

}
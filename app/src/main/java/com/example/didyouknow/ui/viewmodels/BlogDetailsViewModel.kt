package com.example.didyouknow.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.datasource.FirebaseBlogsDatasource
import com.example.didyouknow.other.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlogDetailsViewModel @Inject constructor(
    val blogsDatasource: FirebaseBlogsDatasource
):ViewModel() {

    private var _blog: MutableLiveData<Resources<BlogPost?>> = MutableLiveData()
    val blog get() = _blog

    private var blogDocId:String? = null

    init {
        _blog.postValue(Resources.loading( null ))
    }

    fun initializeViewModel(blogId:String){

        blogDocId = blogId
        viewModelScope.launch {
            val result = blogsDatasource.fetchBlogById(blogId)
            delay(3000 )
            _blog.postValue( result )
        }

    }


}
package com.example.didyouknow.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.datasource.FirebaseBlogsDatasource
import com.example.didyouknow.other.BlogPostEditing
import com.example.didyouknow.other.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddpostFragmentViewModel @Inject constructor(
    val blogsDatasource: FirebaseBlogsDatasource
) : BlogPostEditing() {

    private fun isTitleValid():Boolean{
        return if (postTitle.value?.isNotEmpty() == true){
            if(postTitle.value!![0] == ' '){
                _postTitle.postValue(postTitle.value!!.removePrefix(" "))
                isTitleValid()
            }
            true
        }else{
            false
        }
    }


    fun postBlog():Resources<Boolean>{

        val isValidBlogPost = isTitleValid() && isContentvalid() && isImgLinkvalid()
        val generatedBlogId = generateBlogDocId()
        val blogToPost = prepareBlogPost(generatedBlogId)

        var postingBlogStatus: Resources<Boolean> =  Resources.loading(false)

        if(isValidBlogPost){
            runBlocking {
                postingBlogStatus = blogsDatasource.postBlog(blogToPost)
            }
        }else{
            Log.d("AddPostViewModelLogs","Invalid Blog found")
            postingBlogStatus = Resources.loading(false)
        }

        return postingBlogStatus
    }

    private fun prepareBlogPost( docId:String ):BlogPost{
        return BlogPost(
            content = postContent.value!!,
            date = Calendar.getInstance().time,
            title= postTitle.value!!,
            articleId = docId,
            imageUrl = postimgLink.value!!,

        )


    }

    private fun generateBlogDocId():String{
        return postTitle.value!!.replace(" ","-")
    }

}
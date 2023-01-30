package com.example.didyouknow.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.datasource.FirebaseBlogsDatasource
import com.example.didyouknow.other.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddpostFragmentViewModel @Inject constructor(
    val blogsDatasource: FirebaseBlogsDatasource
) : ViewModel() {

    private var isvalidImage:Boolean = true

    private var _postTitle: MutableLiveData<String> = MutableLiveData("")
    val postTitle get() = _postTitle as LiveData<String>

    private var _postContent: MutableLiveData<String> = MutableLiveData("")
    val postContent get() = _postContent as LiveData<String>

    private var _postImgLink: MutableLiveData<String> = MutableLiveData("")
    val postimgLink get() = _postImgLink as LiveData<String>




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

    private fun isContentvalid():Boolean{
        return postTitle.value?.isNotEmpty() == true
    }


    private fun isImgLinkvalid():Boolean = ( postimgLink.value?.isNotEmpty() == true && isvalidImage )

    fun postBlog():Resources<Boolean>{

        val isValidBlogPost = isTitleValid() && isContentvalid() && isImgLinkvalid()
        val generatedBlogId = generateBlogDocId()
        val blogToPost = prepareBlogPost(generatedBlogId)

        var postingBlogStatus: Resources<Boolean> =  Resources.loading(false)

        if(isValidBlogPost){
            viewModelScope.launch {
                postingBlogStatus= blogsDatasource.postBlog(blogToPost)
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

    fun setImageValidity(isvalid:Boolean){
        isvalidImage = isvalid
    }

    fun updateBlogTitleTxt(newTxt:String)= _postTitle.postValue(newTxt)

    fun updateBlogContentTxt(newTxt:String) = _postContent.postValue(newTxt)

    fun updateBlogImageLinkTxt(newTxt:String) = _postImgLink.postValue(newTxt)

}
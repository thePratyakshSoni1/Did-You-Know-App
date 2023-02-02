package com.example.didyouknow.other

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.didyouknow.data.entities.BlogPost
import kotlinx.coroutines.runBlocking
import java.util.*

abstract class BlogPostEditing: ViewModel() {


    protected var isvalidImage:Boolean = true

    protected var _postTitle: MutableLiveData<String> = MutableLiveData("")
    val postTitle get() = _postTitle as LiveData<String>

    protected var _postContent: MutableLiveData<String> = MutableLiveData("")
    val postContent get() = _postContent as LiveData<String>

    protected var _postImgLink: MutableLiveData<String> = MutableLiveData("")
    val postimgLink get() = _postImgLink as LiveData<String>

    protected fun isContentvalid():Boolean{
        return if(postContent.value?.isNotEmpty() == true){
            Log.d("BlogPostEditingLogs","Invalid Content")
            true
        }else{
            false
        }
    }

    protected fun isImgLinkvalid():Boolean {
        return if(postimgLink.value?.isNotEmpty() == true && isvalidImage){
            Log.d("BlogPostEditingLogs","Invalid Image Link")
            true
        }else{
            false
        }
    }



    protected fun isTitleValid():Boolean{
        return if (postTitle.value?.isNotEmpty() == true){
            if(postTitle.value!![0] == ' '){
                _postTitle.postValue(postTitle.value!!.removePrefix(" "))
                isTitleValid()
            }
            true
        }else{
            Log.d("BlogPostEditingLogs","Invalid Title")
            false
        }
    }


    fun setImageValidity(isvalid:Boolean){
        isvalidImage = isvalid
    }

    fun updateBlogTitleTxt(newTxt:String)= _postTitle.postValue(newTxt)

    fun updateBlogContentTxt(newTxt:String) = _postContent.postValue(newTxt)

    fun updateBlogImageLinkTxt(newTxt:String) = _postImgLink.postValue(newTxt)


}
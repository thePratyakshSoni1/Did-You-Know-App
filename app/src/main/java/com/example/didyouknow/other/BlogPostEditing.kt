package com.example.didyouknow.other

import android.net.Uri
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

    private var _postImgLink: MutableLiveData<String> = MutableLiveData()
    val postimgLink get() = _postImgLink as LiveData<String>

    protected val _isLocalImage = MutableLiveData<Boolean>(false)
    val isLocalImage get() = _isLocalImage as LiveData<Boolean>

    protected var _imageUri: Uri? = null
    val imageUri get() = _imageUri

    private var _isPostImgLinkUpdated: Boolean = false
    val isPostImgLinkUpdated: Boolean get() = _isPostImgLinkUpdated

    private var _blogPostingStatus = MutableLiveData<Resources<Boolean?>>()
    val blogPostingStatus get() = _blogPostingStatus as LiveData<Resources<Boolean?>>


    protected fun isContentvalid():Boolean{
        return if(postContent.value?.isNotEmpty() == true){
            true
        }else{
            Log.d("BlogPostEditingLogs","Invalid Content")
            false
        }
    }

    protected fun isImgLinkvalid():Boolean {

        return if(isLocalImage.value == true && imageUri != null) imageUri!!.isAbsolute && imageUri.toString().isNotEmpty()
        else (postimgLink.value?.toString()?.isNotEmpty() == true && isvalidImage)

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

    fun postValueToPostImageLink(uri:String){
        _isPostImgLinkUpdated = false
        _postImgLink.postValue(uri)
    }

    fun postImageLinkUpdateState(isUpdated:Boolean){
        _isPostImgLinkUpdated = true
    }

    fun updateBlogTitleTxt(newTxt:String)= _postTitle.postValue(newTxt)

    fun updateBlogContentTxt(newTxt:String) = _postContent.postValue(newTxt)

    fun updateBlogImageLinkTxt(newTxt:String) = _postImgLink.postValue(newTxt)


}
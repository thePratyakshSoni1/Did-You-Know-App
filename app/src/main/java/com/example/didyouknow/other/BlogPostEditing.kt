package com.example.didyouknow.other

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.didyouknow.data.remote.BlogsDatabase
import com.example.didyouknow.datasource.FirebaseBlogsDatasource
import kotlinx.coroutines.runBlocking

abstract class BlogPostEditing: ViewModel() {

    val LocalImageTxtPlaceholder = "LOCAL IMAGE ADDED"

    protected var isvalidImage:Boolean = true

    protected var _postTitle: MutableLiveData<String> = MutableLiveData("")
    val postTitle get() = _postTitle as LiveData<String>

    protected var _postContent: MutableLiveData<String> = MutableLiveData("")
    val postContent get() = _postContent as LiveData<String>

    private var _postImgLink: MutableLiveData<String> = MutableLiveData()
    val postimgLink get() = _postImgLink as LiveData<String>


    private var _postImgName: String? = null
    val postImgName get() = _postImgName as String?

    protected val _isLocalImage = MutableLiveData<Boolean>(false)
    val isLocalImage get() = _isLocalImage as LiveData<Boolean>

    protected var _imageUri: Uri? = null
    val imageUri get() = _imageUri

    private var _isPostImgLinkUpdated: Boolean = false
    val isPostImgLinkUpdated: Boolean get() = _isPostImgLinkUpdated

    private var _blogPostingStatus = MutableLiveData<Resources<Boolean?>>()
    val blogPostingStatus get() = _blogPostingStatus as LiveData<Resources<Boolean?>>


    fun setImageLocalUri(uri: Uri?){
        _isLocalImage.postValue(uri != null)
        Log.d("BlgEditingVmLogs","LocalImageUri set to ${isLocalImage.value} because uri is ($uri == null) = ${uri == null}")
        Log.d("BlgEditingVmLogs","because uri is $uri ")
        _imageUri = uri
    }
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

    fun postValueToPostImageLink(imageLink:Pair<String?,Uri>?){

        if(imageLink != null ){
            _isPostImgLinkUpdated = false
            _postImgName = imageLink.first
            _postImgLink.postValue(imageLink.second.toString())
        }else{
            _postImgLink.postValue("")
        }

    }

    fun postImageLinkUpdateState(isUpdated:Boolean){
        _isPostImgLinkUpdated = true
    }


    protected fun uploadImageToFirebase( blogsDatasource: FirebaseBlogsDatasource ):Resources<Pair<String,Uri>?>{
        lateinit var imageUploadStatus:Resources<Pair<String,Uri>?>
        return if(imageUri != null ){
            runBlocking {
                Log.d("ImageUploadblogEditingLogs","uploading from  uri: ${imageUri.toString()}")
                imageUploadStatus = blogsDatasource.uploadImageForBlog(uri = imageUri!!)
            }
            imageUploadStatus
        }else{
            Log.d("AddPostViewModelLogs","Null Image uri while uploading to firebase storage")
            imageUploadStatus = Resources.error(null,"URI IS NULL")
            imageUploadStatus
        }

    }


    fun updateBlogTitleTxt(newTxt:String)= _postTitle.postValue(newTxt)

    fun updateBlogContentTxt(newTxt:String) = _postContent.postValue(newTxt)

    fun updateBlogImageLinkTxt(newTxt:String) = _postImgLink.postValue(newTxt)


}
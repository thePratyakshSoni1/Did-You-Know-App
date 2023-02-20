package com.example.didyouknow.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.data.remote.FirebaseDocFieldNames
import com.example.didyouknow.data.remote.FirebaseDocFields
import com.example.didyouknow.datasource.FirebaseBlogsDatasource
import com.example.didyouknow.other.BlogPostEditing
import com.example.didyouknow.other.Resources
import com.example.didyouknow.other.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val LOGTAG = "BlogsDetailVMLogs"
@HiltViewModel
class BlogDetailsViewModel @Inject constructor(
    val blogsDatasource: FirebaseBlogsDatasource
):BlogPostEditing() {

    private var _blog: MutableLiveData<Resources<BlogPost?>> = MutableLiveData()
    val blog get() = _blog

    private var _isEditingMode:MutableLiveData<Boolean> = MutableLiveData(false)
    val isEditingMode get() = _isEditingMode as LiveData<Boolean>

    private var _isRefreshing:MutableLiveData<Boolean> = MutableLiveData(false)
    val isRefreshing get() = _isRefreshing as LiveData<Boolean>

    private var blogDocId:String? = null

    init {
        _blog.postValue(Resources.loading( null ))
    }

    fun initializeViewModel(
        blogId:String,
        openForEditMode:Boolean
    ){

        blogDocId = blogId
        viewModelScope.launch {
            _isRefreshing.postValue(true)
            val result = blogsDatasource.fetchBlogById(blogId)
            delay(3000 )
            _blog.postValue( result )
            if(openForEditMode) _isEditingMode.postValue(true)
            _isRefreshing.postValue(false)

        }

    }

    fun refreshBlog(){
        _isRefreshing.postValue(true)
        viewModelScope.launch {
            val result = blogsDatasource.fetchBlogById(blogDocId!!)
            _blog.postValue( result )
            _isRefreshing.postValue(false)
        }
    }


    fun setEditingMode(setToEditingMode:Boolean){
        _isEditingMode.postValue(setToEditingMode)

        if(!setToEditingMode){
            blog.value?.data?.let{
                _postTitle.postValue( it.title )
                _postContent.postValue( it.content )
                postValueToPostImageLink(Pair(it.imageName, Uri.parse(it.imageUrl)))
            }
        }
    }

    private fun getInfoAboutFieldsToUpdate():List<FirebaseDocFields>{

        val fieldsToUpdate = mutableListOf<FirebaseDocFields>()
        blog.value?.data.let {

            if(it?.title != postTitle.value){
                fieldsToUpdate.add(FirebaseDocFields.TITLE)
            }

            if(it?.content != postContent.value){
                fieldsToUpdate.add(FirebaseDocFields.CONTENT)
            }

            if( it?.imageUrl != postimgLink.value.toString() ){
                fieldsToUpdate.add(FirebaseDocFields.IMAGE_URL)
            }

        }

        return fieldsToUpdate
    }

    fun updateBlog(): Resources<Boolean>{

        val isBlogValid = isTitleValid() && isContentvalid() && isImgLinkvalid()
        val fieldsToUpdate = getInfoAboutFieldsToUpdate()

        val blogUpdatestatus:MutableList<Resources<String>> = mutableListOf()

        if(!isBlogValid){
            return Resources.error(false, "Your Blog isn't valid")
        }

        if(fieldsToUpdate.isNotEmpty()){
            runBlocking {

                for(items in fieldsToUpdate){

                    when(items){

                        FirebaseDocFields.TITLE -> {
                            val stats =  blogsDatasource.updateBlogTitle(postTitle.value!!, blog.value?.data?.articleId!!)
                            blogUpdatestatus.add(Resources(stats.status, "title", null))
                        }
                        FirebaseDocFields.CONTENT -> {
                            val stats =  blogsDatasource.updateBlogContent(postContent.value!!, blog.value?.data?.articleId!!)
                            blogUpdatestatus.add(Resources(stats.status, "content", null))
                        }

                        FirebaseDocFields.IMAGE_URL -> {

                            val stats:Resources<Boolean> = if(isLocalImage.value == true && imageUri != null ){
                                val imageUploadTask = uploadImageToFirebase(blogsDatasource)
                                if(imageUploadTask.status == Status.SUCCESS){
                                    val imageLink = imageUploadTask.data
                                    viewModelScope.launch{
                                        postValueToPostImageLink(imageLink)
                                    }
                                    var delayTaken = 0L

                                    //postImgLink is updating late so waiting for that
                                    while(!isPostImgLinkUpdated && delayTaken < 5000L ){
                                        delayTaken+= 200L
                                        delay(200L)
                                    }

                                    if(isPostImgLinkUpdated){
                                        logConsole("Link we got: ${postimgLink.value} from ${imageLink.toString()}")
                                        logConsole(imageLink.toString())
                                        blogsDatasource.updateBlogImage(postimgLink.value.toString(), blog.value?.data?.articleId!!)
                                    }else{
                                        logConsole("Error attaching link to blogPost, value update timeout")
                                        val deleteImageTask = blogsDatasource.deleteBlogImage(imageUploadTask.data!!.first)
                                        logConsole("Deleteing uploaded image success: ${deleteImageTask.data}")
                                        Resources.error(false,"Error attaching link to blogPost\nPlease  try again")
                                    }
                                }else{
                                    Resources.error(false, "Image can't be uploaded")
                                }
                            }else{
                                blogsDatasource.updateBlogImage(postimgLink.value.toString(), blog.value?.data?.articleId!!)
                            }
                            if(stats.status == Status.SUCCESS){
                                viewModelScope.launch {
                                    if(!blog.value?.data?.imageName.isNullOrEmpty()){
                                        val imgDeleteTask = blogsDatasource.deleteBlogImage(blog.value?.data?.imageName!!)
                                        if(imgDeleteTask.status == Status.SUCCESS){
                                            logConsole("Previous Image delted successfully")
                                        }else{
                                            logConsole("Failed deleting Previous Image ")
                                        }
                                    }
                                }
                            }
                            blogUpdatestatus.add(Resources(stats.status, "image", null))
                        }

                        else -> Unit
                    }
                }
            }

        }else{
            return Resources.error(false, "Blog Already Updated")
        }

        return generateUpdateStats(blogUpdatestatus)

    }

    private fun logConsole(text:String){
        Log.d(LOGTAG, text)
    }
    private fun generateUpdateStats(blogUpdatestatus:List<Resources<String>>):Resources<Boolean>{

        var returnMsg = "Unable to update: "
        var noOfFailedUpdations = 0

        for(items in blogUpdatestatus){
            if(items.status != Status.SUCCESS){
                noOfFailedUpdations++
                returnMsg += items.data
            }
        }

        if( noOfFailedUpdations == 0 ){
            return Resources.success(true)
        }else if( noOfFailedUpdations > 0 ){
            blogUpdatestatus.forEach {
                if(it.status == Status.ERROR){
                    returnMsg += "${it.message}\n"
                }
            }
            return Resources.partialSuccess(true, returnMsg)
        }else{
            return Resources.error(false, "Blog Updation failed")
        }

    }



}
package com.example.didyouknow.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.didyouknow.data.entities.BlogPost
import com.example.didyouknow.datasource.FirebaseBlogsDatasource
import com.example.didyouknow.other.BlogPostEditing
import com.example.didyouknow.other.Resources
import com.example.didyouknow.other.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddpostFragmentViewModel @Inject constructor(
    val blogsDatasource: FirebaseBlogsDatasource
) : BlogPostEditing() {


    suspend fun postBlog():Resources<Boolean>{

        val isValidBlogPost = isTitleValid() && isContentvalid() && isImgLinkvalid()
        val generatedBlogId = generateBlogDocId()

        var postingBlogStatus: Resources<Boolean> =  Resources.loading(false)

        if(isValidBlogPost){
                val imageUploadTask:Resources<Uri?> = uploadImageToFirebase()
                if(imageUploadTask.status == Status.SUCCESS){
                    val imageLink = imageUploadTask.data
                    viewModelScope.launch{
                        postValueToPostImageLink(imageLink.toString())
                    }
                    var delayTaken = 0L
                    while(!isPostImgLinkUpdated && delayTaken < 5000L ){
                        delayTaken+= 200L
                        delay(200L)
                    }
                    if(isPostImgLinkUpdated){
                        Log.d("AddPostFragmentLogs","Link we got: ${postimgLink.value} from ${imageLink.toString()}")
                        Log.d("AddPostFragmentLogs",imageLink.toString())
                        val blogToPost = prepareBlogPost(generatedBlogId)
                        postingBlogStatus = blogsDatasource.postBlog(blogToPost)
                    }else{
                        Log.d("AddPostFragmentLogs","Error attaching link to blogPost, value update timeout")
                        postingBlogStatus = Resources.error(false,"Error attaching link to blogPost\nPlease  try again")
                    }
                }
                else Log.d("AddPostViewModelLogs","Image not uploaded ${imageUploadTask.message}")

        }else{
            Log.d("AddPostViewModelLogs","Invalid Blog found")
            postingBlogStatus = Resources.loading(false)
        }

        return postingBlogStatus
    }

    private fun uploadImageToFirebase(  ):Resources<Uri?>{
        lateinit var imageUploadStatus:Resources<Uri?>
        return if(imageUri != null ){
            runBlocking {
                Log.d("AddPostFragmentLogs","uploading from  uri: ${imageUri.toString()}")
                imageUploadStatus = blogsDatasource.uploadImageForBlog(uri = imageUri!!)
            }
            imageUploadStatus
        }else{
            Log.d("AddPostViewModelLogs","Null Image uri while uploading to firebase storage")
            imageUploadStatus = Resources.error(null,"URI IS NULL")
            imageUploadStatus
        }

    }

    fun setImageUri(uri: Uri?){
        _isLocalImage.postValue(uri != null)
        _imageUri = uri
    }

    private fun prepareBlogPost( docId:String ):BlogPost{
        return BlogPost(
            content = postContent.value!!,
            date = Calendar.getInstance().time,
            title= postTitle.value!!,
            articleId = docId,
            imageUrl = postimgLink.value.toString(),

        )


    }

    private fun generateBlogDocId():String{
        return postTitle.value!!.replace(" ","-")
    }

}
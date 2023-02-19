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
            lateinit var blogToPost:BlogPost
            postingBlogStatus = if(isLocalImage.value == true){
            val imageUploadTask:Resources<Pair<String,Uri>?> = uploadImageToFirebase()
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
                    blogToPost = prepareBlogPost(generatedBlogId)
                    blogsDatasource.postBlog(blogToPost)
                }else{
                    logConsole("Error attaching link to blogPost, value update timeout")
                    val deleteImageTask = blogsDatasource.deleteBlogImage(imageUploadTask.data!!.first)
                    logConsole("Deleteing uploaded image success: ${deleteImageTask.data}")
                    Resources.error(false,"Error attaching link to blogPost\nPlease  try again")
                }
            }else{
                Resources.error(false, "Image cann't be uploaded")
            }

        }else{
            blogToPost = prepareBlogPost(generatedBlogId)
            blogsDatasource.postBlog(blogToPost)
        }


        }else{
            Log.d("AddPostViewModelLogs","Invalid Blog found")
            postingBlogStatus = Resources.error(false, "Invalid Blog")
        }

        return postingBlogStatus
    }

    private fun uploadImageToFirebase(  ):Resources<Pair<String,Uri>?>{
        lateinit var imageUploadStatus:Resources<Pair<String,Uri>?>
        return if(imageUri != null ){
            runBlocking {
                logConsole("uploading from  uri: ${imageUri.toString()}")
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
            imageName= postImgName

        )


    }

    private fun logConsole(text:String){
       Log.d("AddPostFragmentLogs",text)
    }

    private fun generateBlogDocId():String{
        return postTitle.value!!.replace(" ","-")
    }

}
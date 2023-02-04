package com.example.didyouknow.ui.viewmodels

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
                _postImgLink.postValue( it.imageUrl )
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

            if(it?.imageUrl != postimgLink.value){
                fieldsToUpdate.add(FirebaseDocFields.IMAGE_URL)
            }

        }

        return fieldsToUpdate
    }

    fun updateBlog(): Resources<Boolean>{

        val isBlogValid = isTitleValid() && isContentvalid() && isImgLinkvalid()
        val fieldsToUpdate = getInfoAboutFieldsToUpdate()

        val blogUpdatestatus:MutableList<Resources<Boolean>> = mutableListOf()

        if(!isBlogValid){
            return Resources.error(false, "Your Blog isn't valid")
        }

        if(fieldsToUpdate.isNotEmpty()){
            runBlocking {

                for(items in fieldsToUpdate){

                    when(items){

                        FirebaseDocFields.TITLE -> {
                            val stats =  blogsDatasource.updateBlogTitle(postTitle.value!!, blog.value?.data?.articleId!!)
                            blogUpdatestatus.add(stats)
                        }
                        FirebaseDocFields.CONTENT -> {
                            val stats =  blogsDatasource.updateBlogContent(postContent.value!!, blog.value?.data?.articleId!!)
                            blogUpdatestatus.add(stats)
                        }

                        FirebaseDocFields.IMAGE_URL -> {
                            val stats =  blogsDatasource.updateBlogImage(postimgLink.value!!, blog.value?.data?.articleId!!)
                            blogUpdatestatus.add(stats)
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

    private fun generateUpdateStats(blogUpdatestatus:List<Resources<Boolean>>):Resources<Boolean>{

        var returnMsg = ""
        var allFieldsAreUpdated = true

        for(items in blogUpdatestatus){
            if(items.status != Status.SUCCESS){
                allFieldsAreUpdated = false
                break
            }
        }

        if( !allFieldsAreUpdated ){

            blogUpdatestatus.forEach {
                if(it.status == Status.ERROR){
                    returnMsg += "${it.message}\n"
                }
            }

        }

        return if(allFieldsAreUpdated) Resources.success(true) else Resources.error(false, returnMsg)
    }


}
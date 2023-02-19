package com.example.didyouknow.data.remote

import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.example.didyouknow.other.Resources
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.UUID

private const val IMAGEBDLOGSTAG = "ImageStorageDBLogs"

class ImageStorageDatabase {

    private val firebaseStorageInstance = FirebaseStorage.getInstance()
    private val firebaseStorageRef = firebaseStorageInstance.getReference(STORAGEUTILS.BLOGS_IMAGE_STORAGE_PATH)

    fun uploadImage(uri:Uri):Resources<Pair<String,Uri>?> {

        lateinit var results:Resources<Pair<String,Uri>?>
        runBlocking {

            var isUploadSuccess:Boolean = false
            val imageName = UUID.randomUUID().toString().split("-")[4]
            try{
                val fileRef = firebaseStorageRef.child(imageName)
                val task = fileRef.putFile(uri)
                task.await()
                isUploadSuccess = if(task.isSuccessful){
                    Log.d(IMAGEBDLOGSTAG, "Image Upload task success")
                    true
                }else {
                    Log.d(IMAGEBDLOGSTAG, "Image Upload task Failed")
                    false
                }

            }catch (e: Exception){
                Log.d(IMAGEBDLOGSTAG,"ERROR UPLOADING IMAGE: ${e.message}")

            }

            results = if(isUploadSuccess) getImageDownloadLinkForBlog(imageName = imageName)
            else Resources.error(null, "Failed")
        }
        return results
    }

    fun getImageDownloadLinkForBlog(imageName:String):Resources<Pair<String,Uri>?>{
        lateinit var results:Resources<Pair<String,Uri>?>
        runBlocking {

            try{
                val task = firebaseStorageRef.child(imageName).downloadUrl
                Log.d(IMAGEBDLOGSTAG,"Fetching image link completeListener")
                task.await()
                results = if(task.isSuccessful) {
                    Log.d(IMAGEBDLOGSTAG,"SUCCESS Fetching image link")
                    Resources.success(Pair(imageName, task.result))
                }
                else {
                    Log.d(IMAGEBDLOGSTAG,"Failed Fetching image link link")
                    Resources.error(null, "Error fetching downloading")
                }
                Log.d(IMAGEBDLOGSTAG,"COMPLETED Await image fetching")

            }catch (e: Exception){
                Log.d(IMAGEBDLOGSTAG,"ERROR Fetching image link: ${e.message}")
            }
        }
        return results
    }

    private object STORAGEUTILS{

        var BLOGS_IMAGE_STORAGE_PATH = "blogImageUploads"

    }

    fun deleteBlogImage(imageName:String):Resources<Boolean>{

        val imgRef = firebaseStorageRef.child(imageName)
        lateinit var results:Resources<Boolean>
        runBlocking {
            val deleteImageTask = imgRef.delete()
            deleteImageTask.await()
            results = if( deleteImageTask.isSuccessful ) Resources.success(true)
            else Resources.error(false, "Failed to delete image from databsse")
        }
        return  results

    }

}

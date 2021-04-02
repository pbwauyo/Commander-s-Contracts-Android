package com.example.commanderscontracts

import android.net.Uri
import com.example.commanderscontracts.models.UserContract
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object FirebaseUtils {

    suspend fun uploadPhotos(photosUri: ArrayList<File>): List<UserContract> {
        val storageRef = Firebase.storage.reference
        val photosUrls = ArrayList<UserContract>()
        val uploadedPhotosUriLink = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            (photosUri.indices).map { index ->
                async(Dispatchers.IO) {
                    uploadPhoto(storageRef, photosUri[index])
                }
            }
        }.awaitAll()

        uploadedPhotosUriLink.forEach { photoUriLink -> photosUrls.add(UserContract("","","","","","","","",""))
        }
        //"","",photoUriLink.toString())
        return photosUrls
    }


    private suspend fun uploadPhoto(storageRef: StorageReference, photoFile: File): Uri {
        val fileName = UUID.randomUUID().toString()
        val fileUri = Uri.fromFile(photoFile)

        return storageRef.child(fileName)
            .putFile(fileUri)
            .await()
            .storage
            .downloadUrl
            .await()
    }
}
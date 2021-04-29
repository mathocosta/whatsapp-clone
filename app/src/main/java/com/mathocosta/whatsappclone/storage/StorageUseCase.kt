package com.mathocosta.whatsappclone.storage

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import com.mathocosta.whatsappclone.auth.AuthRequiredUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

interface StorageUseCase : AuthRequiredUseCase {
    val imageFileMetadata
        get() = storageMetadata {
            contentType = "image/jpg"
        }

    fun getProfileImageRef(): StorageReference? {
        val currentUser = currentUser ?: return null
        return StorageGateway.getImagesRef(currentUser).child(PROFILE_IMG_PATH)
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    suspend fun uploadImageFromUserAt(location: String, imageBitmap: Bitmap): Uri? =
        withContext(Dispatchers.IO) {
            val currentUser = currentUser ?: return@withContext null
            val imagesRef = StorageGateway.getImagesRef(currentUser)
            val uploadedImageRef = imagesRef.child(location)

            uploadImageAt(uploadedImageRef, imageBitmap)
        }

    suspend fun uploadImageAt(storageRef: StorageReference, imageBitmap: Bitmap): Uri? =
        withContext(Dispatchers.IO) {
            val imageBytes = compressBitmap(imageBitmap)
            storageRef.putBytes(imageBytes, imageFileMetadata).continueWithTask { uploadTask ->
                if (!uploadTask.isSuccessful) {
                    uploadTask.exception?.let { throw it }
                }

                storageRef.downloadUrl
            }.await()
        }

    companion object {
        const val TAG = "STORAGE"
        private const val PROFILE_IMG_PATH = "profile.jpg"
    }
}
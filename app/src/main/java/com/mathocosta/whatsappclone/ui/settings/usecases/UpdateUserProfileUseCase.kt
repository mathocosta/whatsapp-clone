package com.mathocosta.whatsappclone.ui.settings.usecases

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.StorageReference
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.storage.StorageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class UpdateUserProfileUseCase : StorageUseCase, DatabaseUseCase {
    suspend fun uploadProfileImage(imageBitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        getProfileImageRef()?.let {
            it.putBytes(data, imageFileMetadata).await()
            getDownloadLinkFrom(it)
        } ?: Uri.EMPTY
    }

    suspend fun uploadProfileImage(fileUri: Uri): Uri = withContext(Dispatchers.IO) {
        getProfileImageRef()?.let {
            it.putFile(fileUri, imageFileMetadata).await()
            getDownloadLinkFrom(it)
        } ?: Uri.EMPTY
    }

    private suspend fun getDownloadLinkFrom(storageRef: StorageReference): Uri =
        storageRef.downloadUrl.await()

    suspend fun updateCurrentUserProfile(userProfile: UserProfile) = withContext(Dispatchers.IO) {
        val request = userProfileChangeRequest {
            displayName = userProfile.username
            photoUri = Uri.parse(userProfile.photoUrl)
        }

        currentUser?.updateProfile(request)?.await()

        val userRef = getUserRef()
        userRef?.setValue(userProfile)?.await()
    }
}
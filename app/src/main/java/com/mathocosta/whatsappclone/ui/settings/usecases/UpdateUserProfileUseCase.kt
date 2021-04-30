package com.mathocosta.whatsappclone.ui.settings.usecases

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.StorageReference
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.storage.StorageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class UpdateUserProfileUseCase : StorageUseCase, DatabaseUseCase {
    suspend operator fun invoke(userProfile: UserProfile, imageBitmap: Bitmap) : UserProfile {
        return withContext(Dispatchers.IO) {
            val downloadUrl = async { uploadImageFromUserAt(PROFILE_IMG_PATH, imageBitmap) }

            userProfile.photoUrl = downloadUrl.await().toString()

            val updateDatabase = async { invoke(userProfile) }
            updateDatabase.await()

            userProfile
        }
    }

    suspend operator fun invoke(userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            getUserRef()?.setValue(userProfile)?.await()
        }
    }

    private suspend fun updateAuth(userProfile: UserProfile) {
        currentUser?.let {
            val request = userProfileChangeRequest {
                displayName = userProfile.username
                photoUri = Uri.parse(userProfile.photoUrl)
            }

            it.updateProfile(request).await()
        }
    }

    companion object {
        private const val PROFILE_IMG_PATH = "profile.jpg"
    }
}
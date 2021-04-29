package com.mathocosta.whatsappclone.ui.settings

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.ui.settings.usecases.GetUserProfileUseCase
import com.mathocosta.whatsappclone.ui.settings.usecases.UpdateUserProfileUseCase
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val getUserProfile = GetUserProfileUseCase()
    private val updateUserProfile = UpdateUserProfileUseCase()

    private val currentUserProfile: MutableLiveData<UserProfile> by lazy {
        MutableLiveData<UserProfile>()
    }

    fun loadUserProfile(): LiveData<UserProfile> {
        viewModelScope.launch {
            val userProfile = getUserProfile()
            currentUserProfile.value = userProfile
        }

        return currentUserProfile
    }

    fun setProfileImage(imageBitmap: Bitmap) {
        viewModelScope.launch {
            val imageUri = updateUserProfile.uploadProfileImage(imageBitmap)
            currentUserProfile.value?.run {
                val updatedProfile = copy(photoUrl = imageUri.toString())
                //updateUserProfile.updateCurrentUserProfile(updatedProfile)
                currentUserProfile.postValue(updatedProfile)
            }
        }
    }

    fun setProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            val downloadUri = updateUserProfile.uploadProfileImage(imageUri)
            currentUserProfile.value?.run {
                val updatedProfile = copy(photoUrl = downloadUri.toString())
                //updateUserProfile.updateCurrentUserProfile(updatedProfile)
                currentUserProfile.postValue(updatedProfile)
            }
        }
    }
}
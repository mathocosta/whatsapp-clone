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

    private val _currentUserProfile = MutableLiveData<UserProfile?>()

    val currentUserProfile: LiveData<UserProfile?>
        get() = _currentUserProfile

    init {
        viewModelScope.launch {
            _currentUserProfile.value = getUserProfile()
        }
    }

    fun setProfileImage(imageBitmap: Bitmap) {
        viewModelScope.launch {
            _currentUserProfile.value?.let {
                val updatedUserProfile = updateUserProfile(it, imageBitmap)
                _currentUserProfile.value = updatedUserProfile
            }
        }
    }
}
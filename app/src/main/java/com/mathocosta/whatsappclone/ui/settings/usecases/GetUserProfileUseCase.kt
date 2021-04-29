package com.mathocosta.whatsappclone.ui.settings.usecases

import android.util.Log
import com.google.firebase.database.ktx.getValue
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.storage.StorageUseCase
import kotlinx.coroutines.tasks.await

class GetUserProfileUseCase: StorageUseCase, DatabaseUseCase {
    suspend operator fun invoke(): UserProfile? {
        val userRef = getUserRef() ?: return null

        return try {
            userRef.get().await().getValue<UserProfile>()
        } catch (ex: Exception) {
            Log.d(StorageUseCase.TAG, "getUsername: ", ex)
            null
        }
    }
}
package com.mathocosta.whatsappclone.db

import android.util.Log
import com.mathocosta.whatsappclone.db.model.UserProfile

class SaveUserProfileUseCase : DatabaseUseCase {
    fun saveUser(userProfile: UserProfile) {
        val userRef = getUserRef() ?: return
        userRef.setValue(userProfile).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DATABASE", "saveUser: isSuccessful")
            } else {
                Log.w("DATABASE", "saveUser: ", task.exception)
            }
        }
    }
}
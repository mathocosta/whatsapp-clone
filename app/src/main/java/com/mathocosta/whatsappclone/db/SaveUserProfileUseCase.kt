package com.mathocosta.whatsappclone.db

import com.google.android.gms.tasks.Task
import com.mathocosta.whatsappclone.db.model.UserProfile

class SaveUserProfileUseCase : DatabaseUseCase {
    fun saveUser(userProfile: UserProfile) : Task<Void>? {
        return getUserRef()?.setValue(userProfile)
    }
}
package com.mathocosta.whatsappclone.ui.login

import com.mathocosta.whatsappclone.auth.AuthGateway
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginUseCase : DatabaseUseCase {
    val auth = AuthGateway.auth

    suspend fun signIn(email: String, password: String) {
        withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password).await()
        }
    }

    suspend fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun saveUser(userProfile: UserProfile) {
        getUserRef()?.setValue(userProfile)?.await()
    }
}
package com.mathocosta.whatsappclone.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mathocosta.whatsappclone.db.model.UserProfile

object AuthGateway {
    val auth = Firebase.auth

    fun getCurrentUserUid(): String? = auth.currentUser?.uid

    fun signOut() {
        auth.signOut()
    }
}
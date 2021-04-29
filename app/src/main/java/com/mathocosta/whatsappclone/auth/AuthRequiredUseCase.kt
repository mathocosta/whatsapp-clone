package com.mathocosta.whatsappclone.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRequiredUseCase {
    val currentUser: FirebaseUser?
        get() = AuthGateway.auth.currentUser
}
package com.mathocosta.whatsappclone.ui.chat

import com.google.firebase.auth.FirebaseUser
import com.mathocosta.whatsappclone.auth.AuthGateway
import com.mathocosta.whatsappclone.db.model.Message

class IsFromCurrentUserValidator(private val currentUser: FirebaseUser? = AuthGateway.auth.currentUser) {
    operator fun invoke(message: Message): Boolean = message.senderId == currentUser?.uid
}
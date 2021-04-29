package com.mathocosta.whatsappclone.ui.home.chats

import com.google.firebase.database.ktx.getValue
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GetChatsUseCase : DatabaseUseCase {
    suspend operator fun invoke(): List<Chat> = withContext(Dispatchers.IO) {
        currentUser?.let { user ->
            val dataSnapshot = getChatsRef().child(user.uid).get().await()

            dataSnapshot.children.mapNotNull { childSnapshot ->
                childSnapshot.getValue<Chat>()
            }
        } ?: emptyList()
    }
}
package com.mathocosta.whatsappclone.ui.chat.usecases

import com.google.firebase.database.ktx.getValue
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.Chat
import com.mathocosta.whatsappclone.db.model.Group
import com.mathocosta.whatsappclone.db.model.Message
import com.mathocosta.whatsappclone.db.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SaveChatUseCase : DatabaseUseCase {
    suspend operator fun invoke(receiverProfile: UserProfile, lastMessage: Message) =
        withContext(Dispatchers.IO) {
            getCurrentUserProfile()?.let {
                val deferreds = listOf(
                    async { saveChat(it, receiverProfile, lastMessage) },
                    async { saveChat(receiverProfile, it, lastMessage) }
                )

                deferreds.awaitAll()
            }
        }

    private suspend fun saveChat(
        fromProfile: UserProfile,
        toProfile: UserProfile,
        lastMessage: Message
    ) {
        val fromId = fromProfile.uid
        val toId = toProfile.uid

        if (fromId != null && toId != null) {
            val chat = Chat(
                senderId = fromId,
                receiverId = toId,
                lastMessage = lastMessage,
                receiverProfile = toProfile
            )
            getChatsRef().child(fromId).child(toId).setValue(chat).await()
        }
    }

    suspend operator fun invoke(group: Group, lastMessage: Message) = withContext(Dispatchers.IO) {
        val deferreds = group.members.map {
            async {
                val fromId = it.uid
                val toId = group.id

                if (fromId != null && toId != null) {
                    val chat = Chat(
                        senderId = fromId,
                        receiverId = toId,
                        lastMessage = lastMessage,
                        group = group
                    )
                    getChatsRef().child(fromId).child(toId).setValue(chat).await()
                }
            }
        }

        deferreds.awaitAll()
    }
}
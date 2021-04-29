package com.mathocosta.whatsappclone.ui.chat.usecases

import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.Group
import com.mathocosta.whatsappclone.db.model.Message
import com.mathocosta.whatsappclone.storage.StorageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SendMessageUseCase : DatabaseUseCase, StorageUseCase {
    /**
     * Send message action
     *
     * For that, 2 message objects are created, one for each user, in their private chat. It allows
     * that if one deletes the chat, the other user keeps a copy that will not be deleted.
     */
    suspend operator fun invoke(message: Message) = withContext(Dispatchers.IO) {
        val senderId = currentUser?.uid
        val receiverId = message.receiverId

        message.apply {
            this.senderId = senderId
        }

        if (senderId != null && receiverId != null) {
            val deferreds = listOf(
                async { saveMessage(message, senderId, receiverId) },
                async { saveMessage(message, receiverId, senderId) }
            )

            deferreds.awaitAll()
        }
    }

    /**
     * Send message to group action
     *
     * For that, is created messages to all members of the group in their private chat.
     */
    suspend operator fun invoke(message: Message, group: Group) = withContext(Dispatchers.IO) {
        val senderId = currentUser?.uid
        val senderUserProfile = async { getCurrentUserProfile() }
        val receiverId = message.receiverId

        message.apply {
            this.senderId = senderId
            this.senderUserProfile = senderUserProfile.await()
        }

        if (receiverId != null) {
            val deferreds = group.members.mapNotNull {
                val parentId = it.uid ?: return@mapNotNull null
                async { saveMessage(message, parentId, receiverId) }
            }

            deferreds.awaitAll()
        }
    }

    private suspend fun saveMessage(message: Message, parentUid: String, childUid: String) {
        getMessagesRef().child(parentUid).child(childUid).push().setValue(message).await()
    }
}
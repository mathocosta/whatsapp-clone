package com.mathocosta.whatsappclone.ui.group

import android.graphics.Bitmap
import android.net.Uri
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.Chat
import com.mathocosta.whatsappclone.db.model.Group
import com.mathocosta.whatsappclone.storage.StorageGateway
import com.mathocosta.whatsappclone.storage.StorageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CreateGroupUseCase : DatabaseUseCase, StorageUseCase {
    suspend operator fun invoke(group: Group, imageBitmap: Bitmap?) {
        withContext(Dispatchers.IO) {
            val groupRef = getGroupsRef().push()
            val groupId = groupRef.key

            val imageUri = async {
                if (imageBitmap != null && groupId != null) {
                    uploadGroupImage(imageBitmap, groupId)
                } else {
                    null
                }
            }

            group.apply {
                photoUrl = imageUri.await().toString()
                id = groupId
            }

            groupRef.setValue(group).await()

            val deferreds = group.members.map {
                async {
                    createGroupChat(
                        Chat(
                            senderId = it.uid,
                            receiverId = group.id,
                            group = group
                        )
                    )
                }
            }

            deferreds.awaitAll()
        }
    }

    private suspend fun createGroupChat(chat: Chat) {
        val fromId = chat.senderId
        val toId = chat.receiverId

        if (fromId != null && toId != null) {
            getChatsRef().child(fromId).child(toId).setValue(chat).await()
        }
    }

    private suspend fun uploadGroupImage(imageBitmap: Bitmap, groupId: String): Uri? {
        return StorageGateway.getGroupImagesRef().child("$groupId.jpg").let {
            uploadImageAt(it, imageBitmap)
        }
    }
}
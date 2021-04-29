package com.mathocosta.whatsappclone.ui.chat.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathocosta.whatsappclone.auth.AuthGateway
import com.mathocosta.whatsappclone.db.model.Group
import com.mathocosta.whatsappclone.db.model.Message
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.ui.chat.usecases.GetChatMessagesUseCase
import com.mathocosta.whatsappclone.ui.chat.usecases.SaveChatUseCase
import com.mathocosta.whatsappclone.ui.chat.usecases.SendMessageUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveChat: SaveChatUseCase,
    private val getChatMessages: GetChatMessagesUseCase
) : ViewModel(),
    GetChatMessagesUseCase.OnMessagesChangeListener {
    private val chatLiveData = MutableLiveData<List<Message>>()

    var receiverUserProfile: UserProfile? = null
    var group: Group? = null

    private fun getReceiverId(): String? = when {
        receiverUserProfile != null -> receiverUserProfile?.uid
        group != null -> group?.id
        else -> null
    }

    fun getMessages(): LiveData<List<Message>> {
        AuthGateway.getCurrentUserUid()?.let { senderId ->
            getReceiverId()?.let { receiverId ->
                getChatMessages(
                    GetChatMessagesUseCase.Request(senderId, receiverId),
                    this
                )
            }
        }

        return chatLiveData
    }

    fun sendMessage(text: String?) {
        viewModelScope.launch {
            getReceiverId()?.let { receiverId ->
                val lastMessage = Message(text = text, receiverId = receiverId)

                receiverUserProfile?.let {
                    sendMessageUseCase(lastMessage)
                    saveChat(it, lastMessage)
                }

                group?.let {
                    sendMessageUseCase(lastMessage, it)
                    saveChat(it, lastMessage)
                }
            }
        }
    }

    fun sendMessage(imageBitmap: Bitmap) {
        viewModelScope.launch {
            val imageId = UUID.randomUUID().toString()
            val location = "messages/$imageId.jpg"
            val receiverId = getReceiverId() ?: return@launch

            val photoUrl = async { sendMessageUseCase.uploadImageFromUserAt(location, imageBitmap) }

            val lastMessage = Message(receiverId = receiverId, image = photoUrl.await().toString())

            receiverUserProfile?.let {
                sendMessageUseCase(lastMessage)
                saveChat(it, lastMessage)
            }

            group?.let {
                sendMessageUseCase(lastMessage, it)
                saveChat(it, lastMessage)
            }
        }
    }

    override fun onNewMessage(message: Message) {
        val actualChat = chatLiveData.value
        if (actualChat == null) {
            chatLiveData.value = listOf(message)
        } else {
            chatLiveData.value = actualChat + message
        }
    }

    override fun onCleared() {
        super.onCleared()
        getChatMessages.removeListeners()
    }
}
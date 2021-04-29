package com.mathocosta.whatsappclone.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.ui.chat.usecases.GetChatMessagesUseCase
import com.mathocosta.whatsappclone.ui.chat.usecases.SaveChatUseCase
import com.mathocosta.whatsappclone.ui.chat.usecases.SendMessageUseCase

class ChatViewModelFactory(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveChatUseCase: SaveChatUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(
            sendMessageUseCase,
            saveChatUseCase,
            getChatMessagesUseCase
        ) as T
    }
}
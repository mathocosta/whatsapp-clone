package com.mathocosta.whatsappclone.ui.home.chats

import androidx.lifecycle.*

class ChatsViewModel(private val getChats: GetChatsUseCase) : ViewModel() {
    val chatsLiveData = liveData {
        val chats = getChats()
        emit(chats)
    }
}
package com.mathocosta.whatsappclone.ui.home.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatsViewModelFactory(private val getChatsUseCase: GetChatsUseCase) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatsViewModel(getChatsUseCase) as T
    }
}
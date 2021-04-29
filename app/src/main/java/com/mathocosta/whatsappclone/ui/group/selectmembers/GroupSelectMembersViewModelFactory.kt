package com.mathocosta.whatsappclone.ui.group.selectmembers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mathocosta.whatsappclone.ui.home.contacts.GetContactsUseCase

class GroupSelectMembersViewModelFactory(private val getContacts: GetContactsUseCase) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GroupSelectMembersViewModel(getContacts) as T
    }
}
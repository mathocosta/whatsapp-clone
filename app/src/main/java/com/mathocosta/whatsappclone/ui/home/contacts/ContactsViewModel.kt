package com.mathocosta.whatsappclone.ui.home.contacts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathocosta.whatsappclone.db.model.UserProfile
import kotlinx.coroutines.launch

class ContactsViewModel : ViewModel() {
    private val getContactsUseCase = GetContactsUseCase()
    private val contactsLiveData = MutableLiveData<List<UserProfile>>()

    private val groupItem: UserProfile
        get() = UserProfile(username = "new-group")

    fun getContacts(): LiveData<List<UserProfile>> {
        viewModelScope.launch {
            try {
                contactsLiveData.value = getContactsUseCase().toMutableList().apply {
                    add(0, groupItem)
                }
            } catch (ex: Exception) {
                Log.w("CONTACTS", ex)
            }
        }

        return contactsLiveData
    }
}
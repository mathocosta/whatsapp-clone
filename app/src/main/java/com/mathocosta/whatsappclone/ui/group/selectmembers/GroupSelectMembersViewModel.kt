package com.mathocosta.whatsappclone.ui.group.selectmembers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.mathocosta.whatsappclone.ui.home.contacts.GetContactsUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile

class GroupSelectMembersViewModel(private val getContacts: GetContactsUseCase) : ViewModel() {
    val contactsLiveData = liveData {
        try {
            val contacts = getContacts()
            emit(contacts)
        } catch (ex: Exception) {
            Log.w("GROUP", ex)
        }
    }

    private val selectedContactsLiveData =
        MutableLiveData<List<UserProfile>>()

    fun getSelectedContacts(): LiveData<List<UserProfile>> = selectedContactsLiveData

    fun selectMember(userProfile: UserProfile) {
        val actualValue = selectedContactsLiveData.value
        if (actualValue != null) {
            selectedContactsLiveData.value = actualValue + userProfile
        } else {
            selectedContactsLiveData.value = listOf(userProfile)
        }
    }

    fun deselectMember(userProfile: UserProfile) {
        val actualValue = selectedContactsLiveData.value

        if (actualValue != null) {
            val mutableCopy = actualValue.toMutableList()
            mutableCopy.remove(userProfile)
            selectedContactsLiveData.value = mutableCopy
        }
    }
}
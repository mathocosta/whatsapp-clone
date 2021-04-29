package com.mathocosta.whatsappclone.ui.group

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathocosta.whatsappclone.db.model.Group
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class CreateGroupViewModel : ViewModel() {
    private val createGroupUseCase = CreateGroupUseCase()

    private var groupImageBitmap: Bitmap? = null

    private val _groupLiveData = MutableLiveData(Group())

    val groupLiveData: LiveData<Group>
        get() = _groupLiveData

    private val _navigateToGroupChat = SingleLiveEvent<Any>()

    val navigateToGroupChat: LiveData<Any>
        get() = _navigateToGroupChat

    fun setGroupMembers(groupMembers: List<UserProfile>) {
        _groupLiveData.value?.apply {
            members = groupMembers
        }
    }

    fun setGroupImage(bitmap: Bitmap) {
        groupImageBitmap = bitmap
    }

    fun saveGroup(name: String) {
        viewModelScope.launch {
            _groupLiveData.value?.let {
                it.name = name
                try {
                    createGroupUseCase(it, groupImageBitmap)
                    Log.d("CREATE_GROUP", "Group creation is successful")
                    _navigateToGroupChat.call()
                } catch (ex: Exception) {
                    Log.w("CREATE_GROUP", ex)
                }
            }
        }
    }
}
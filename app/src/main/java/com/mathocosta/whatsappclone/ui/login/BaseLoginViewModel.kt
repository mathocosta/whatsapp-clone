package com.mathocosta.whatsappclone.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mathocosta.whatsappclone.utils.SingleLiveEvent

abstract class BaseLoginViewModel : ViewModel() {
    protected val _navigateToHome = SingleLiveEvent<Any>()

    val navigateToHome: LiveData<Any>
        get() = _navigateToHome

    protected val _errorMessages = MutableLiveData(ErrorMessages())

    val errorMessages: LiveData<ErrorMessages>
        get() = _errorMessages

    protected val _isLoading = MutableLiveData(false)

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    data class ErrorMessages(
        var emailError: String? = null,
        var passwordError: String? = null,
        var undefinedError: String? = null
    )
}
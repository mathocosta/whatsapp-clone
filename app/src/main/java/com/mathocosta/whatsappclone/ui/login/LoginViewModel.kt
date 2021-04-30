package com.mathocosta.whatsappclone.ui.login

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mathocosta.whatsappclone.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class LoginViewModel : BaseLoginViewModel() {
    private val loginUseCase = LoginUseCase()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                loginUseCase.signIn(email, password)

                Log.d("SIGN_IN", "login: isSuccessful")
                _navigateToHome.call()
            } catch (ex: Exception) {
                Log.w("SIGN_IN", "login: ", ex)
                when (ex) {
                    is FirebaseAuthInvalidCredentialsException ->
                        _errorMessages.value = ErrorMessages(passwordError = ex.message)
                    is FirebaseAuthInvalidUserException ->
                        _errorMessages.value = ErrorMessages(emailError = ex.message)
                    else ->
                        _errorMessages.value = ErrorMessages(undefinedError = ex.message)
                }
            }
        }
    }
}
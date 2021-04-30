package com.mathocosta.whatsappclone.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class SignUpViewModel : BaseLoginViewModel() {
    private val loginUseCase = LoginUseCase()

    fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            try {
                val ioDispatcher = Dispatchers.IO

                val deferreds = listOf(
                    async(ioDispatcher) { loginUseCase.createUser(email, password) },
                    async(ioDispatcher) {
                        loginUseCase.saveUser(
                            UserProfile(
                                email = email,
                                username = username
                            )
                        )
                    }
                )

                deferreds.awaitAll()

                Log.d("SIGN_UP", "signUp: isSuccessful")
                _navigateToHome.call()
            } catch (ex: Exception) {
                Log.w("SIGN_UP", "signUp: ", ex)
                when (ex) {
                    is FirebaseAuthWeakPasswordException ->
                    _errorMessages.value = ErrorMessages(passwordError = ex.message)
                    is FirebaseAuthInvalidCredentialsException,
                    is FirebaseAuthUserCollisionException ->
                        _errorMessages.value = ErrorMessages(emailError = ex.message)
                    else -> _errorMessages.value = ErrorMessages(undefinedError = ex.message)
                }
            }
        }
    }
}
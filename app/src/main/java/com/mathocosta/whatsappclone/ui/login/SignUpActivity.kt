package com.mathocosta.whatsappclone.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.auth.AuthGateway
import com.mathocosta.whatsappclone.databinding.ActivitySignUpBinding
import com.mathocosta.whatsappclone.db.SaveUserProfileUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.ui.home.HomeActivity

class SignUpActivity : AppCompatActivity() {
    private val saveUseCase = SaveUserProfileUseCase()
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun showChats() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    fun onSignUpClick(view: View) {
        val emailText = binding.signUpEmailIptLayout.editText?.text.toString()
        val passwordText = binding.signUpPasswordIptLayout.editText?.text.toString()
        val nameText = binding.signUpNameIptLayout.editText?.text.toString()

        if (emailText.isEmpty()) {
            binding.signUpEmailIptLayout.error = getString(R.string.required_field_error)
            return
        }

        if (passwordText.isEmpty()) {
            binding.signUpPasswordIptLayout.error = getString(R.string.required_field_error)
            return
        }

        AuthGateway.auth.createUserWithEmailAndPassword(emailText, passwordText)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SIGN_UP", "createUserWithEmailAndPassword: isSuccessful")
                    saveUseCase.saveUser(UserProfile(email = emailText, username = nameText))
                    showChats()
                } else {
                    Log.w("SIGN_UP", "createUserWithEmailAndPassword: ", task.exception)
                    task.exception?.let {
                        when (it) {
                            is FirebaseAuthWeakPasswordException ->
                                binding.signUpPasswordIptLayout.error = it.reason
                            is FirebaseAuthInvalidCredentialsException,
                            is FirebaseAuthUserCollisionException ->
                                binding.signUpEmailIptLayout.error = it.message
                            else -> Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
    }
}
package com.mathocosta.whatsappclone.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.auth.AuthGateway
import com.mathocosta.whatsappclone.databinding.ActivityLoginBinding
import com.mathocosta.whatsappclone.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    private val auth by lazy {
        AuthGateway.auth
    }

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            showChats()
        }
    }

    fun showSignUp(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun showChats() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    fun onLoginClick(view: View) {
        val emailText = binding.loginEmailIptLayout.editText?.text.toString()
        val passwordText = binding.loginPasswordIptLayout.editText?.text.toString()

        if (emailText.isEmpty()) {
            binding.loginEmailIptLayout.error = getString(R.string.required_field_error)
            return
        }

        if (passwordText.isEmpty()) {
            binding.loginPasswordIptLayout.error = getString(R.string.required_field_error)
            return
        }

        auth.signInWithEmailAndPassword(emailText, passwordText)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SIGN_UP", "signInWithEmailAndPassword: isSuccessful")
                    showChats()
                } else {
                    Log.w("SIGN_UP", "signInWithEmailAndPassword: ", task.exception)
                    task.exception?.let {
                        when (it) {
                            is FirebaseAuthInvalidCredentialsException ->
                                binding.loginPasswordIptLayout.error = it.message
                            is FirebaseAuthInvalidUserException ->
                                binding.loginEmailIptLayout.error = it.message
                            else -> Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
    }
}
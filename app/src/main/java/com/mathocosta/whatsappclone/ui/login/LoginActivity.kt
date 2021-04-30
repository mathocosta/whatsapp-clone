package com.mathocosta.whatsappclone.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.auth.AuthGateway
import com.mathocosta.whatsappclone.databinding.ActivityLoginBinding
import com.mathocosta.whatsappclone.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        viewModel.navigateToHome.observe(this) {
            showChats()
        }

        viewModel.errorMessages.observe(this) { errorMessages ->
            binding.loginEmailIptLayout.error = errorMessages.emailError
            binding.loginPasswordIptLayout.error = errorMessages.passwordError
            errorMessages.undefinedError?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (AuthGateway.auth.currentUser != null) {
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

        viewModel.login(emailText, passwordText)
    }
}
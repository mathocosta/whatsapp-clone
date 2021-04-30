package com.mathocosta.whatsappclone.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.ActivitySignUpBinding
import com.mathocosta.whatsappclone.ui.home.HomeActivity

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        viewModel.navigateToHome.observe(this) {
            showChats()
        }

        viewModel.errorMessages.observe(this) { errorMessages ->
            errorMessages.emailError?.let {
                binding.signUpEmailIptLayout.error = it
            }

            errorMessages.passwordError?.let {
                binding.signUpPasswordIptLayout.error = it
            }

            errorMessages.undefinedError?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
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

        viewModel.signUp(emailText, passwordText, nameText)
    }
}
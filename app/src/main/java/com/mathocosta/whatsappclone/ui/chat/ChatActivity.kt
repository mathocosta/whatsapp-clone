package com.mathocosta.whatsappclone.ui.chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.ActivityChatBinding
import com.mathocosta.whatsappclone.db.model.Group
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.ui.chat.usecases.GetChatMessagesUseCase
import com.mathocosta.whatsappclone.ui.chat.usecases.SaveChatUseCase
import com.mathocosta.whatsappclone.ui.chat.usecases.SendMessageUseCase
import com.mathocosta.whatsappclone.ui.chat.viewmodel.ChatViewModel
import com.mathocosta.whatsappclone.ui.chat.viewmodel.ChatViewModelFactory

class ChatActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ChatViewModelFactory(
                SendMessageUseCase(),
                SaveChatUseCase(),
                GetChatMessagesUseCase()
            )
        ).get(ChatViewModel::class.java)

        handleIntentExtras()
        setSendMessageClick()
        setupRecyclerView()
    }

    private fun handleIntentExtras() {
        intent.extras?.let {
            if (it.containsKey(GROUP_OBJ_EXTRA)) {
                val group = it.getSerializable(GROUP_OBJ_EXTRA) as Group
                setupActionBar(group.name, group.photoUrl)
                viewModel.group = group
            } else if (it.containsKey(USER_PROFILE_OBJ_EXTRA)) {
                val userProfile = it.getSerializable(USER_PROFILE_OBJ_EXTRA) as UserProfile
                setupActionBar(userProfile.username, userProfile.photoUrl)
                viewModel.receiverUserProfile = userProfile
            }
        }
    }

    private fun setupActionBar(title: String, photoUri: String?) {
        val toolbar = binding.chatActToolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.chatProfileTxtView.text = title
        if (photoUri != null) {
            Glide.with(this)
                .load(photoUri)
                .into(binding.chatProfileImgView)
        } else {
            binding.chatProfileImgView.setImageResource(R.drawable.padrao)
        }
    }

    private fun setSendMessageClick() {
        with(binding.chatActContent) {
            chatSendMessageFab.setOnClickListener {
                val messageText = chatMessageEdtTxt.text.toString()

                if (messageText.isNotEmpty()) {
                    viewModel.sendMessage(messageText)
                    chatMessageEdtTxt.setText("")
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val recViewAdapter = ChatRecViewAdapter()

        viewModel.getMessages().observe(this) {
            recViewAdapter.submitList(it)
        }

        with(binding.chatActContent) {
            chatRecView.layoutManager = LinearLayoutManager(this@ChatActivity)
            chatRecView.adapter = recViewAdapter
        }
    }

    fun onCameraImgClick(view: View) {
        val missingPermissions = arrayOf(Manifest.permission.CAMERA).filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions, 1)
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
                startActivityForResult(cameraIntent, IMAGE_CAPTURE_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            viewModel.sendMessage(imageBitmap)
        }
    }

    companion object {
        private const val USER_PROFILE_OBJ_EXTRA = "user-profile"
        private const val GROUP_OBJ_EXTRA = "group"
        private const val IMAGE_CAPTURE_REQUEST_CODE = 101

        fun getIntent(context: Context, userProfile: UserProfile): Intent =
            Intent(context, ChatActivity::class.java).apply {
                putExtra(USER_PROFILE_OBJ_EXTRA, userProfile)
            }

        fun getIntent(context: Context, group: Group): Intent =
            Intent(context, ChatActivity::class.java).apply {
                putExtra(GROUP_OBJ_EXTRA, group)
            }
    }
}
package com.mathocosta.whatsappclone.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.ActivitySettingsBinding
import kotlinx.coroutines.*

class SettingsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        setupActionBar()
        setupObserver()
        askForPermissionsIfNeeded()
    }

    private fun setupActionBar() {
        val toolbar = binding.settingsToolbar.mainToolbar
        toolbar.title = getString(R.string.settings_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupObserver() {
        viewModel.loadUserProfile().observe(this) { userProfile ->
            binding.settingsNameIptLayout.editText?.setText(userProfile.username)
            Glide.with(this@SettingsActivity)
                .load(userProfile.photoUrl)
                .into(binding.settingsProfileImgView)
        }
    }

    private fun askForPermissionsIfNeeded() {
        val missingPermissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions, 1)
        }
    }

    fun onCameraButtonClick(view: View) {
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), IMAGE_CAPTURE_REQUEST_CODE)
    }

    fun onPhotoLibraryButtonClick(view: View) {
        startActivityForResult(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ), PHOTO_LIBRARY_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                IMAGE_CAPTURE_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.settingsProfileImgView.setImageBitmap(bitmap)
                    viewModel.setProfileImage(bitmap)
                }
                PHOTO_LIBRARY_REQUEST_CODE -> {
                    val uri = data?.data as Uri
                    binding.settingsProfileImgView.setImageURI(uri)
                    viewModel.setProfileImage(uri)
                }
            }
        }
    }

    companion object {
        private const val IMAGE_CAPTURE_REQUEST_CODE = 101
        private const val PHOTO_LIBRARY_REQUEST_CODE = 102
    }
}
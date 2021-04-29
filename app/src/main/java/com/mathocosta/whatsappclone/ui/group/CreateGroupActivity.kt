package com.mathocosta.whatsappclone.ui.group

import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.ActivityCreateGroupBinding
import com.mathocosta.whatsappclone.db.model.UserProfile
import java.io.Serializable

class CreateGroupActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCreateGroupBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: CreateGroupViewModel
    private val recViewAdapter = GroupSelectedMembersListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.run {
            setSupportActionBar(this)
            title = getString(R.string.new_group)
            subtitle = getString(R.string.subtitle_create_group_act_toolbar)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)

        intent.extras?.let {
            val groupMembers = it.getSerializable(GROUP_MEMBERS_LIST_EXTRA) as List<UserProfile>
            viewModel.setGroupMembers(groupMembers)
        }

        setupRecView()
        setupObservers()

    }

    private fun updateMembersCountLabel(count: Int) {
        binding.contentCreateGroup.groupMembersCountTxtView.text =
            getString(R.string.group_members_label, count)
    }

    private fun setupRecView() = with(binding.contentCreateGroup) {
        groupMembersRecView.layoutManager = LinearLayoutManager(
            this@CreateGroupActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        groupMembersRecView.adapter = recViewAdapter
        groupMembersRecView.setHasFixedSize(true)
    }

    private fun setupObservers() {
        viewModel.groupLiveData.observe(this) {
            updateMembersCountLabel(it.members.size)
            recViewAdapter.submitList(it.members)
        }

        viewModel.navigateToGroupChat.observe(this) {
            Toast.makeText(this, "Grupo Salvo", Toast.LENGTH_LONG).show()
        }
    }

    fun onSelectGroupImageClick(view: View) {
        startActivityForResult(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ), GROUP_IMAGE_PICK_REQUEST_CODE
        )
    }

    fun onSaveFabClick(view: View) {
        val editText = binding.contentCreateGroup.createGroupNameEdtTxt
        val nameText = editText.text.toString()

        if (nameText.isNotEmpty()) {
            viewModel.saveGroup(nameText)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == GROUP_IMAGE_PICK_REQUEST_CODE) {
            try {
                val uri = data?.data as Uri
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }
                binding.contentCreateGroup.createGroupImgView.setImageURI(uri)
                viewModel.setGroupImage(bitmap)
            } catch (ex: Exception) {
                Log.w("CREATE_GROUP", ex)
            }
        }
    }

    companion object {
        private const val GROUP_IMAGE_PICK_REQUEST_CODE = 101
        private const val GROUP_MEMBERS_LIST_EXTRA = "group-members"

        fun getIntent(context: Context, groupMembers: List<UserProfile>): Intent =
            Intent(context, CreateGroupActivity::class.java).apply {
                putExtra(GROUP_MEMBERS_LIST_EXTRA, groupMembers as Serializable)
            }
    }
}
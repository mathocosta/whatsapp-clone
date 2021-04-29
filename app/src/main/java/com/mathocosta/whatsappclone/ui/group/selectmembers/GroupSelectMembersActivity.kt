package com.mathocosta.whatsappclone.ui.group.selectmembers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.ActivityGroupSelectMembersBinding
import com.mathocosta.whatsappclone.ui.home.contacts.GetContactsUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.ui.group.CreateGroupActivity
import com.mathocosta.whatsappclone.ui.group.GroupSelectedMembersListAdapter
import com.mathocosta.whatsappclone.ui.home.contacts.ContactsListAdapter

class GroupSelectMembersActivity : AppCompatActivity(), ContactsListAdapter.OnItemClickListener,
    GroupSelectedMembersListAdapter.OnItemClickListener {
    private val binding by lazy {
        ActivityGroupSelectMembersBinding.inflate(layoutInflater)
    }

    private val contentBinding
        get() = binding.contentGroupSelectedMembers

    private lateinit var viewModel: GroupSelectMembersViewModel

    private val contactsRecViewAdapter = ContactsListAdapter(this)
    private val selectedMembersRecViewAdapter = GroupSelectedMembersListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateToolbarSubtitle()

        viewModel =
            ViewModelProvider(
                this,
                GroupSelectMembersViewModelFactory(GetContactsUseCase())
            ).get(GroupSelectMembersViewModel::class.java)

        setupRecViews()
        setContentObservers()
    }

    private fun setupRecViews() = with(contentBinding) {
        groupContactsRecView.layoutManager =
            LinearLayoutManager(this@GroupSelectMembersActivity)
        groupContactsRecView.adapter = contactsRecViewAdapter

        groupSelectedRecView.layoutManager = LinearLayoutManager(
            this@GroupSelectMembersActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        groupSelectedRecView.adapter = selectedMembersRecViewAdapter
    }

    private fun setContentObservers() {
        viewModel.contactsLiveData.observe(this) {
            contactsRecViewAdapter.submitList(it)
            updateToolbarSubtitle()
        }

        viewModel.getSelectedContacts().observe(this) {
            selectedMembersRecViewAdapter.submitList(it)
            updateToolbarSubtitle()
        }
    }

    private fun updateToolbarSubtitle() {
        val contactsCount = contactsRecViewAdapter.itemCount
        val selectedContactsCount = selectedMembersRecViewAdapter.itemCount

        binding.toolbar.subtitle = getString(
            R.string.group_select_members_act_toolbar,
            selectedContactsCount,
            contactsCount
        )
    }

    override fun onItemClick(userProfile: UserProfile, position: Int) {
        viewModel.selectMember(userProfile)
    }

    override fun onSelectedItemClick(userProfile: UserProfile) {
        viewModel.deselectMember(userProfile)
    }


    fun onContinueFabClick(view: View) {
        val selectedMembers = selectedMembersRecViewAdapter.currentList
        val intent = CreateGroupActivity.getIntent(this, selectedMembers)
        startActivity(intent)
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, GroupSelectMembersActivity::class.java)
    }
}
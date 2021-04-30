package com.mathocosta.whatsappclone.ui.home.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.FragmentContactsBinding
import com.mathocosta.whatsappclone.db.model.UserProfile
import com.mathocosta.whatsappclone.ui.chat.ChatActivity
import com.mathocosta.whatsappclone.ui.group.selectmembers.GroupSelectMembersActivity

class ContactsFragment : Fragment(), ContactsListAdapter.OnItemClickListener {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ContactsViewModel

    private val recViewAdapter by lazy {
        ContactsListAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        viewModel.getContacts().observe(viewLifecycleOwner) {
            recViewAdapter.submitList(it)
        }

        val layoutManager = LinearLayoutManager(activity)
        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation).apply {
            ResourcesCompat.getDrawable(resources, R.drawable.divider_item_layer, null)?.let {
                setDrawable(it)
            }
         }

        binding.contactsRecView.layoutManager = layoutManager
        binding.contactsRecView.adapter = recViewAdapter
        binding.contactsRecView.addItemDecoration(itemDecoration)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(userProfile: UserProfile, position: Int) {
        activity?.let {
            val intent = if (userProfile.username == GROUP_ITEM_ID) {
                GroupSelectMembersActivity.getIntent(it)
            } else {
                ChatActivity.getIntent(it, userProfile)
            }

            startActivity(intent)
        }
    }

    companion object {
        const val GROUP_ITEM_ID = "new-group"
    }
}
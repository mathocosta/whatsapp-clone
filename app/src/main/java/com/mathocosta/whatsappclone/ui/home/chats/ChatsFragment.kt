package com.mathocosta.whatsappclone.ui.home.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathocosta.whatsappclone.databinding.FragmentChatsBinding
import com.mathocosta.whatsappclone.db.model.Chat
import com.mathocosta.whatsappclone.ui.chat.ChatActivity
import com.mathocosta.whatsappclone.ui.home.FilterableContent

class ChatsFragment : Fragment(), ChatsListAdapter.OnItemClickListener, FilterableContent {
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private val recViewAdapter by lazy {
        ChatsListAdapter(this)
    }

    private lateinit var viewModel: ChatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)

        binding.chatsRecView.layoutManager = LinearLayoutManager(activity)
        binding.chatsRecView.adapter = recViewAdapter

        viewModel = ViewModelProvider(
            this,
            ChatsViewModelFactory(GetChatsUseCase())
        ).get(ChatsViewModel::class.java)

        viewModel.chatsLiveData.observe(viewLifecycleOwner) {
            recViewAdapter.setChatList(it)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(chat: Chat, position: Int) {
        activity?.let { activity ->
            chat.receiverProfile?.let { userProfile ->
                val intent = ChatActivity.getIntent(activity, userProfile)
                startActivity(intent)
            }

            chat.group?.let { group ->
                val intent = ChatActivity.getIntent(activity, group)
                startActivity(intent)
            }
        }
    }

    override fun getFilter(): Filter = recViewAdapter.filter
}
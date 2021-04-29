package com.mathocosta.whatsappclone.ui.home.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.ChatsListItemBinding
import com.mathocosta.whatsappclone.db.model.Chat
import java.util.*

class ChatsListAdapter(private val clickListener: OnItemClickListener) :
    ListAdapter<Chat, ChatsListAdapter.ViewHolder>(ChatsListAdapter), Filterable {

    private var chatList = listOf<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bindTo(chat) {
            clickListener.onItemClick(chat, position)
        }
    }

    fun setChatList(chatList: List<Chat>) {
        this.chatList = chatList
        submitList(chatList)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Chat>()
                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(chatList)
                } else {
                    for (chat in chatList) {
                        chat.receiverProfile?.run {
                            if (username.toLowerCase(Locale.ROOT)
                                    .contains(constraint.toString().toLowerCase(Locale.ROOT))
                            ) {
                                filteredList.add(chat)
                            }
                        }

                        chat.group?.run {
                            if (name.toLowerCase(Locale.ROOT)
                                    .contains(constraint.toString().toLowerCase(Locale.ROOT))
                            ) {
                                filteredList.add(chat)
                            }
                        }
                    }
                }

                return FilterResults().apply {
                    values = filteredList
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                results?.values?.let {
                    submitList(it as List<Chat>)
                }
            }
        }
    }

    class ViewHolder(private val binding: ChatsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(item: Chat, onClick: () -> Unit) {
            with(binding) {
                item.receiverProfile?.let {
                    listItemTitleTxtView.text = it.username
                    if (it.photoUrl != null) {
                        Glide.with(binding.root.context).load(it.photoUrl).into(listItemImgView)
                    } else {
                        listItemImgView.setImageResource(R.drawable.padrao)
                    }
                }

                item.group?.let {
                    listItemTitleTxtView.text = it.name
                    if (it.photoUrl != null) {
                        Glide.with(binding.root.context).load(it.photoUrl).into(listItemImgView)
                    } else {
                        listItemImgView.setImageResource(R.drawable.padrao)
                    }
                }

                if (item.lastMessage != null) {
                    listItemSubtitleTxtView.text = item.lastMessage.text
                } else {
                    listItemSubtitleTxtView.visibility = View.GONE
                }

                root.setOnClickListener {
                    onClick()
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ChatsListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

                return ViewHolder(binding)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(chat: Chat, position: Int)
    }

    companion object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean =
            (oldItem.senderId == newItem.senderId) && (oldItem.receiverId == newItem.receiverId)

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean = oldItem == newItem
    }
}
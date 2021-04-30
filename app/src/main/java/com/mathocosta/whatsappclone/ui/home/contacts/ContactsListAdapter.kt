package com.mathocosta.whatsappclone.ui.home.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.ChatsListItemBinding
import com.mathocosta.whatsappclone.db.model.UserProfile

class ContactsListAdapter(private val clickListener: OnItemClickListener) :
    ListAdapter<UserProfile, ContactsListAdapter.ViewHolder>(ContactsListAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userProfile = getItem(position)
        holder.bindTo(userProfile) {
            clickListener.onItemClick(userProfile, position)
        }
    }

    class ViewHolder(private val binding: ChatsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context: Context
            get() = binding.root.context

        fun bindTo(item: UserProfile, onClick: () -> Unit) {
            with(binding) {
                val isNewGroupItem = (item.username == GROUP_ITEM_ID)

                if (isNewGroupItem) {
                    listItemTitleTxtView.text = context.getString(R.string.new_group)
                    listItemSubtitleTxtView.visibility = View.GONE
                    listItemImgView.setImageResource(R.drawable.icone_grupo)
                } else {
                    listItemTitleTxtView.text = item.username
                    listItemSubtitleTxtView.text = item.email
                    if (item.photoUrl != null) {
                        Glide.with(context).load(item.photoUrl).into(listItemImgView)
                    } else {
                        listItemImgView.setImageResource(R.drawable.padrao)
                    }
                }

                root.setOnClickListener {
                    onClick()
                }
            }
        }

        companion object {
            const val GROUP_ITEM_ID = "new-group"

            fun from(parent: ViewGroup): ViewHolder {
                val binding = ChatsListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

                return ViewHolder(binding)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(userProfile: UserProfile, position: Int)
    }

    companion object : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem == newItem
        }
    }
}
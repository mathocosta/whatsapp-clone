package com.mathocosta.whatsappclone.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathocosta.whatsappclone.databinding.MessageReceiverListItemBinding
import com.mathocosta.whatsappclone.databinding.MessageSenderListItemBinding
import com.mathocosta.whatsappclone.db.model.Message
import com.mathocosta.whatsappclone.utils.toInt

class ChatRecViewAdapter :
    ListAdapter<Message, ChatRecViewAdapter.MessageViewHolder>(ChatRecViewAdapter) {

    private val isFromCurrentUser = IsFromCurrentUserValidator()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder =
        if (viewType == 1) {
            SenderViewHolder.from(parent)
        } else {
            ReceiverViewHolder.from(parent)
        }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = isFromCurrentUser(getItem(position)).toInt()

    abstract class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindTo(message: Message)
    }

    class SenderViewHolder(private val binding: MessageSenderListItemBinding) :
        MessageViewHolder(binding.root) {

        override fun bindTo(message: Message) {
            with(binding) {
                msgSenderItemTxtView.text = message.text
                val imageUri = message.image
                if (imageUri != null) {
                    Glide.with(binding.root.context).load(imageUri).into(msgSenderItemImgView)
                } else {
                    msgSenderItemImgView.visibility = View.GONE
                }

                message.senderUserProfile?.let {
                    msgSenderItemNameTxtView.text = it.username
                } ?: run {
                    msgSenderItemNameTxtView.visibility = View.GONE
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): SenderViewHolder {
                val binding = MessageSenderListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

                return SenderViewHolder(binding)
            }
        }
    }

    class ReceiverViewHolder(private val binding: MessageReceiverListItemBinding) :
        MessageViewHolder(binding.root) {

        override fun bindTo(message: Message) {
            with(binding) {
                msgReceiverItemTxtView.text = message.text
                val imageUri = message.image
                if (imageUri != null) {
                    Glide.with(binding.root.context).load(imageUri).into(msgReceiverItemImgView)
                } else {
                    msgReceiverItemImgView.visibility = View.GONE
                }

                message.senderUserProfile?.let {
                    msgReceiverItemNameTxtView.text = it.username
                } ?: run {
                    msgReceiverItemNameTxtView.visibility = View.GONE
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ReceiverViewHolder {
                val binding = MessageReceiverListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

                return ReceiverViewHolder(binding)
            }
        }
    }

    companion object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
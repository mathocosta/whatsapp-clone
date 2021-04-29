package com.mathocosta.whatsappclone.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.databinding.SelectedMemberListItemBinding
import com.mathocosta.whatsappclone.db.model.UserProfile

class GroupSelectedMembersListAdapter() :
    ListAdapter<UserProfile, GroupSelectedMembersListAdapter.ViewHolder>(
        GroupSelectedMembersListAdapter
    ) {

    private var clickListener: OnItemClickListener? = null

    constructor(clickListener: OnItemClickListener) : this() {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userProfile = getItem(position)
        holder.bindTo(userProfile) {
            clickListener?.onSelectedItemClick(userProfile)
        }
    }

    class ViewHolder(val binding: SelectedMemberListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(userProfile: UserProfile, onClick: () -> Unit) {
            with(binding) {
                selectedMemberNameTxtView.text = userProfile.username
                if (userProfile.photoUrl != null) {
                    Glide.with(binding.root.context)
                        .load(userProfile.photoUrl)
                        .into(selectedMemberImgView)
                } else {
                    selectedMemberImgView.setImageResource(R.drawable.padrao)
                }

                root.setOnClickListener {
                    onClick()
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = SelectedMemberListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    interface OnItemClickListener {
        fun onSelectedItemClick(userProfile: UserProfile)
    }

    companion object : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean =
            oldItem.uid == newItem.uid

        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean =
            oldItem == newItem
    }
}
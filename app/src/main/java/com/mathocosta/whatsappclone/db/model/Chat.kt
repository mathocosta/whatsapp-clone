package com.mathocosta.whatsappclone.db.model

data class Chat(
    val senderId: String? = null,
    val receiverId: String? = null,
    val lastMessage: Message? = null,
    val receiverProfile: UserProfile? = null,
    val group: Group? = null
)
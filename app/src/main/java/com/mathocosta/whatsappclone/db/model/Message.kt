package com.mathocosta.whatsappclone.db.model

data class Message(
    var senderId: String? = null,
    var senderUserProfile: UserProfile? = null,
    val receiverId: String? = null,
    val text: String? = null,
    val image: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

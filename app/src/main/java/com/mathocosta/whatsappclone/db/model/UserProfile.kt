package com.mathocosta.whatsappclone.db.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class UserProfile(
    @Exclude var uid: String? = null,
    val email: String = "",
    val username: String = "",
    val photoUrl: String? = null
) : Serializable

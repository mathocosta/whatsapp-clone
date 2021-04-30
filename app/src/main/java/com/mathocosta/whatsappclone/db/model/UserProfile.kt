package com.mathocosta.whatsappclone.db.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class UserProfile(
    @Exclude var uid: String? = null,
    var email: String = "",
    var username: String = "",
    var photoUrl: String? = null
) : Serializable

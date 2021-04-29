package com.mathocosta.whatsappclone.db.model

import java.io.Serializable

data class Group(
    var id: String? = null,
    var name: String = "",
    var photoUrl: String? = null,
    var members: List<UserProfile> = emptyList()
) : Serializable

package com.mathocosta.whatsappclone.db

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DatabaseGateway {
    val database = Firebase.database

    init {
        Firebase.database.setPersistenceEnabled(true)
    }
}